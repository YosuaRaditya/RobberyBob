import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.*;


public class RobberyBob {
    private BufferedImage[] sprites = new BufferedImage[16];
    private BufferedImage[] smokeSprites = new BufferedImage[9];
    private int spriteIndex = 0;
    private int smokeIndex = 0;
    // Use float for smoother movement
    private float xPos, yPos;   
    public int x, y;
    public int width = 170, height = 170;
    private String arah = "kanan";
    private boolean isMoving = false;
    private boolean isHiding = false;
    private boolean inHidingArea = false;
    private boolean isPlayingSmoke = false;
    private Set<Integer> keysPressed = new HashSet<>();
    private Timer animTimer;
    private Timer smokeTimer;
    private Timer movementTimer;
    private boolean hasExtraItem = false;
    private boolean hasFinished = false;

    private Runnable onLevelComplete;
    private Runnable onFinish;

    private int visibilityRadius = 150;

    // Stamina system variables
    private float maxStamina = 100.0f;
    private float currentStamina = 100.0f;
    private float staminaDrainRate = 0.5f; // How fast stamina drains when running
    private float staminaRegenRate = 0.2f; // How fast stamina regenerates
    private boolean isRunning = false; // Track if player is running
    private Timer staminaTimer; // Timer to handle stamina changes

    // Movement smoothing variables
    private float moveSpeedNormal = 2.5f;
    private float moveSpeedFast = 5.0f;
    private float currentSpeedX = 0f;
    private float currentSpeedY = 0f;
    private float acceleration = 1.0f;
    private float deceleration = 0.8f;
    private float maxSpeedX = 0f;
    private float maxSpeedY = 0f;
    private float frictionFactor = 0.8f;

    // Add these instance variables to store collision map and panel dimensions
    private BufferedImage currentCollisionMap;
    private int currentPanelW;
    private int currentPanelH;

    private int pendingGold = 0;
    private int grabAbilityLevel = 1;
    private float grabSpeed = 1.0f;
    private int rottenDonutCount = 0;
    private int invisibilityPotionCount = 0;
    
    private boolean isInvisible = false;
    private Timer invisibilityTimer;
    private int invisibilityDuration = 5000;
    
    public void setOnFinish(Runnable onFinish) {
        this.onFinish = onFinish;
    }


    public RobberyBob(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.xPos = startX;
        this.yPos = startY;

        try {
            // Load character sprites
            for (int i = 0; i < 16; i++) {
                sprites[i] = ImageIO.read(new File("RobberyBob/Assets/jalan" + i + ".png"));
            }
            
            // Load smoke sprites
            for (int i = 0; i < 9; i++) {
                smokeSprites[i] = ImageIO.read(new File("RobberyBob/Assets/Asset" + (i+1) + ".png"));
            }
        } catch (IOException e) {
            System.out.println("Error loading sprites: " + e.getMessage());
        }

        animTimer = new Timer(100, e -> {
            if (isMoving) {
                spriteIndex = (spriteIndex + 1) % sprites.length;
            }
        });
        
        // Create smoke timer but don't start it yet
        smokeTimer = new Timer(100, e -> {
            smokeIndex = (smokeIndex + 1) % smokeSprites.length;
            
            // If we've completed a full cycle of the smoke animation when hiding/unhiding
            if (smokeIndex == 0 && isPlayingSmoke) {
                isPlayingSmoke = false;
                ((Timer)e.getSource()).stop(); // Stop the timer when animation completes
            }
        });
        
        // Create continuous movement timer for smooth movement
        movementTimer = new Timer(16, e -> { // ~60 FPS
            updateMovement();
        });
        movementTimer.start();
        
        // Create stamina timer to handle regeneration/drain
        staminaTimer = new Timer(50, e -> { // Update stamina every 50ms
            updateStamina();
        });
        staminaTimer.start();
    }
    
    // Manage stamina regeneration and drain
    private void updateStamina() {
        // Drain stamina when running
        if (isRunning && isMoving) {
            currentStamina = Math.max(0, currentStamina - staminaDrainRate);
        } 
        // Regenerate stamina when not running
        else if (!isRunning && currentStamina < maxStamina) {
            currentStamina = Math.min(maxStamina, currentStamina + staminaRegenRate);
        }
    }
    
    // Get current stamina percentage for display
    public float getStaminaPercentage() {
        return (currentStamina / maxStamina) * 100;
    }

    // Update player position based on current speed
    private void updateMovement() {
        if (isHiding || currentCollisionMap == null) return;
        
        // Apply friction if no keys are pressed
        if (!isMoving) {
            currentSpeedX *= frictionFactor;
            currentSpeedY *= frictionFactor;
            
            // Stop completely if speed is very low
            if (Math.abs(currentSpeedX) < 0.1f) currentSpeedX = 0;
            if (Math.abs(currentSpeedY) < 0.1f) currentSpeedY = 0;
        }
        
        // Calculate new position
        float newX = xPos + currentSpeedX;
        float newY = yPos + currentSpeedY;
        
        // Check if new position is walkable using the stored collision map
        if (isWalkable((int)(newX + width / 2), (int)(newY + height / 2), currentCollisionMap, currentPanelW, currentPanelH)) {
            xPos = newX;
            yPos = newY;
            x = Math.round(xPos);
            y = Math.round(yPos);
            
            // Update hiding area detection
            inHidingArea = isInHidingArea(x + width / 2, y + height / 2, currentCollisionMap, currentPanelW, currentPanelH);
        } else {
            // If collision detected, try moving on X or Y axis separately
            if (isWalkable((int)(newX + width / 2), (int)(yPos + height / 2), currentCollisionMap, currentPanelW, currentPanelH)) {
                xPos = newX;
                x = Math.round(xPos);
            } else if (isWalkable((int)(xPos + width / 2), (int)(newY + height / 2), currentCollisionMap, currentPanelW, currentPanelH)) {
                yPos = newY;
                y = Math.round(yPos);
            }
            
            // Reduce speed when hitting obstacles
            currentSpeedX *= 0.5f;
            currentSpeedY *= 0.5f;
        }
        
        // Update animation state
        if (Math.abs(currentSpeedX) > 0.1f || Math.abs(currentSpeedY) > 0.1f) {
            if (!animTimer.isRunning()) animTimer.start();
        } else {
            isMoving = false;
            if (animTimer.isRunning()) {
                animTimer.stop();
                spriteIndex = 0;
            }
        }
    }

    public void handleKeyPressed(int keyCode, BufferedImage collisionMap, int panelW, int panelH) {
        // Store the collision map and dimensions for use in updateMovement
        this.currentCollisionMap = collisionMap;
        this.currentPanelW = panelW;
        this.currentPanelH = panelH;
        
        keysPressed.add(keyCode);
        // Toggle hiding state when 'I' is pressed and in hiding area
        if (keyCode == KeyEvent.VK_I && inHidingArea) {
            isHiding = !isHiding;

            // Start smoke animation when changing hiding state
            smokeIndex = 0; // Reset to start of animation
            isPlayingSmoke = true; // Mark that we're playing the animation
            if (!smokeTimer.isRunning()) {
                smokeTimer.start();
            }
            return;
        }
        if (keyCode == KeyEvent.VK_D && rottenDonutCount > 0) {
            useRottenDonut();
            return;
        }
        if (keyCode == KeyEvent.VK_P && invisibilityPotionCount > 0) {
            useInvisibilityPotion();
            return;
        }
        // Movement logic
        isMoving = true;

        int dx = 0, dy = 0;

        if (keysPressed.contains(KeyEvent.VK_W) && keysPressed.contains(KeyEvent.VK_D) && keysPressed.contains(KeyEvent.VK_SHIFT)) {
            dx = 10; dy = -10; arah = "kanan_atas";
        } else if (keysPressed.contains(KeyEvent.VK_W) && keysPressed.contains(KeyEvent.VK_A) && keysPressed.contains(KeyEvent.VK_SHIFT)) {
            dx = -10; dy = -10; arah = "kiri_atas";
        } else if (keysPressed.contains(KeyEvent.VK_S) && keysPressed.contains(KeyEvent.VK_D) && keysPressed.contains(KeyEvent.VK_SHIFT)) {
            dx = 10; dy = 10; arah = "kanan_bawah";
        } else if (keysPressed.contains(KeyEvent.VK_S) && keysPressed.contains(KeyEvent.VK_A) && keysPressed.contains(KeyEvent.VK_SHIFT)) {
            dx = -10; dy = 10; arah = "kiri_bawah";
        } else if (keysPressed.contains(KeyEvent.VK_W) && keysPressed.contains(KeyEvent.VK_SHIFT)) {
            dx = 0; dy = -10; arah = "atas";
        } else if (keysPressed.contains(KeyEvent.VK_S) && keysPressed.contains(KeyEvent.VK_SHIFT)) {
            dx = 0; dy = 10; arah = "bawah";
        } else if (keysPressed.contains(KeyEvent.VK_A) && keysPressed.contains(KeyEvent.VK_SHIFT)) {
            dx = -10; dy = 0; arah = "kiri";
        } else if (keysPressed.contains(KeyEvent.VK_D) && keysPressed.contains(KeyEvent.VK_SHIFT)) {
            dx = 10; dy = 0; arah = "kanan";
        } else if (keysPressed.contains(KeyEvent.VK_W) && keysPressed.contains(KeyEvent.VK_D)) {
            dx = 5; dy = -5; arah = "kanan_atas";
        } else if (keysPressed.contains(KeyEvent.VK_W) && keysPressed.contains(KeyEvent.VK_A)) {
            dx = -5; dy = -5; arah = "kiri_atas";
        } else if (keysPressed.contains(KeyEvent.VK_S) && keysPressed.contains(KeyEvent.VK_D)) {
            dx = 5; dy = 5; arah = "kanan_bawah";
        } else if (keysPressed.contains(KeyEvent.VK_S) && keysPressed.contains(KeyEvent.VK_A)) {
            dx = -5; dy = 5; arah = "kiri_bawah";
        } else if (keysPressed.contains(KeyEvent.VK_W)) {
            dx = 0; dy = -5; arah = "atas";
        } else if (keysPressed.contains(KeyEvent.VK_S)) {
            dx = 0; dy = 5; arah = "bawah";
        } else if (keysPressed.contains(KeyEvent.VK_A)) {
            dx = -5; dy = 0; arah = "kiri";
        } else if (keysPressed.contains(KeyEvent.VK_D)) {
            dx = 5; dy = 0; arah = "kanan";
        }

        
        // Don't move if hiding
        if (isHiding) {
            return;
        }
        
        // Set movement direction and speed based on keys pressed
        updateDirectionAndSpeed(collisionMap, panelW, panelH);
    }
    
    private void updateDirectionAndSpeed(BufferedImage collisionMap, int panelW, int panelH) {
        // Update stored collision map and dimensions
        if (collisionMap != null) {
            this.currentCollisionMap = collisionMap;
            this.currentPanelW = panelW;
            this.currentPanelH = panelH;
        }
        
        boolean w = keysPressed.contains(KeyEvent.VK_W);
        boolean a = keysPressed.contains(KeyEvent.VK_A);
        boolean s = keysPressed.contains(KeyEvent.VK_S);
        boolean d = keysPressed.contains(KeyEvent.VK_D);
        boolean shift = keysPressed.contains(KeyEvent.VK_SHIFT);
        
        // Check if player wants to run and has enough stamina
        isRunning = shift && currentStamina > 0;
        
        // Determine current speed based on running state
        float currentMoveSpeed = isRunning ? moveSpeedFast : moveSpeedNormal;
        
        // Calculate direction
        float dirX = 0;
        float dirY = 0;
        
        if (d) dirX += 1;
        if (a) dirX -= 1;
        if (s) dirY += 1;
        if (w) dirY -= 1;
        
        // Normalize diagonal movement
        if (dirX != 0 && dirY != 0) {
            float normalizer = (float)(1.0 / Math.sqrt(2));
            dirX *= normalizer;
            dirY *= normalizer;
        }
        
        // Update target speeds
        maxSpeedX = dirX * currentMoveSpeed;
        maxSpeedY = dirY * currentMoveSpeed;
        
        // Update moving flag
        isMoving = dirX != 0 || dirY != 0;
        
        // Adjust current speed towards target speed (acceleration)
        if (dirX != 0) {
            if (Math.abs(currentSpeedX - maxSpeedX) > acceleration) {
                currentSpeedX += (maxSpeedX > currentSpeedX) ? acceleration : -acceleration;
            } else {
                currentSpeedX = maxSpeedX;
            }
        } else {
            // Apply deceleration when no direction pressed
            if (currentSpeedX > 0) {
                currentSpeedX = Math.max(0, currentSpeedX - deceleration);
            } else if (currentSpeedX < 0) {
                currentSpeedX = Math.min(0, currentSpeedX + deceleration);
            }
        }
        
        if (dirY != 0) {
            if (Math.abs(currentSpeedY - maxSpeedY) > acceleration) {
                currentSpeedY += (maxSpeedY > currentSpeedY) ? acceleration : -acceleration;
            } else {
                currentSpeedY = maxSpeedY;
            }
        } else {
            // Apply deceleration when no direction pressed
            if (currentSpeedY > 0) {
                currentSpeedY = Math.max(0, currentSpeedY - deceleration);
            } else if (currentSpeedY < 0) {
                currentSpeedY = Math.min(0, currentSpeedY + deceleration);
            }
        }
        
        // Update character direction
        updateCharacterDirection();
    }
    
    // Check if player is currently running
    public boolean isRunning() {
        return isRunning && isMoving;
    }

    // Get current stamina value
    public float getCurrentStamina() {
        return currentStamina;
    }

    // Get max stamina value
    public float getMaxStamina() {
        return maxStamina;
    }
    
    // Draw stamina bar
    public void drawStaminaBar(Graphics g) {
        int barWidth = 200;
        int barHeight = 15;
        int x = 20;
        int y = 145;
        
        // Draw outline
        g.setColor(Color.BLACK);
        g.fillRect(x-2, y-2, barWidth+4, barHeight+4);
        
        // Draw background
        g.setColor(Color.GRAY);
        g.fillRect(x, y, barWidth, barHeight);
        
        // Calculate filled portion
        int filledWidth = (int)((currentStamina / maxStamina) * barWidth);
        
        // Choose color based on stamina level
        if (currentStamina > 70) {
            g.setColor(Color.GREEN);
        } else if (currentStamina > 30) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.RED);
        }
        
        // Draw filled portion
        g.fillRect(x, y, filledWidth, barHeight);
        
        // Draw text label
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("STAMINA", x + 5, y + barHeight - 3);
    }
    
    private void updateCharacterDirection() {
        // Calculate the angle of movement to determine character direction
        if (currentSpeedX == 0 && currentSpeedY == 0) return;
        
        float angle = (float) Math.toDegrees(Math.atan2(currentSpeedY, currentSpeedX));
        
        // Convert angle to character direction
        if (angle > -22.5 && angle <= 22.5) {
            arah = "kanan";
        } else if (angle > 22.5 && angle <= 67.5) {
            arah = "kanan_bawah";
        } else if (angle > 67.5 && angle <= 112.5) {
            arah = "bawah";
        } else if (angle > 112.5 && angle <= 157.5) {
            arah = "kiri_bawah";
        } else if (angle > 157.5 || angle <= -157.5) {
            arah = "kiri";
        } else if (angle > -157.5 && angle <= -112.5) {
            arah = "kiri_atas";
        } else if (angle > -112.5 && angle <= -67.5) {
            arah = "atas";
        } else if (angle > -67.5 && angle <= -22.5) {
            arah = "kanan_atas";
        }
    }


    public void handleKeyReleased(int keyCode) {
        keysPressed.remove(keyCode);
        
        // Update movement direction and speed when a key is released
        if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_A || 
            keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_D) {
            updateDirectionAndSpeed(null, 0, 0);
        }
        
        if (keysPressed.isEmpty()) {
            isMoving = false;
            spriteIndex = 0;
            if (animTimer.isRunning()) animTimer.stop();
        }
    }

    public void draw(Graphics g, Component c) {
        if (isHiding) {
            if (isPlayingSmoke && smokeSprites[smokeIndex] != null) {
                // Draw smoke animation when transitioning
                g.drawImage(smokeSprites[smokeIndex], x, y, width, height, c);
            } else {
                // When fully hidden and animation is complete, don't draw anything
                // This makes the player invisible when hiding
            }
        } else {
            if (isPlayingSmoke && smokeSprites[smokeIndex] != null) {
                // Draw smoke animation when unhiding or using invisibility potion
                g.drawImage(smokeSprites[smokeIndex], x, y, width, height, c);
            } else if (sprites[spriteIndex] != null && !isInvisible) { // Don't draw Bob when invisible
                // Draw normal character when not hiding and not invisible
                Graphics2D g2d = (Graphics2D) g.create();
                BufferedImage scaledSprite = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D gScaled = scaledSprite.createGraphics();
                gScaled.drawImage(sprites[spriteIndex], 0, 0, width, height, null);
                gScaled.dispose();


                double angle = 0;
switch (arah) {
    case "kanan":
        angle = Math.PI / 2;
        break;
    case "kiri":
        angle = -Math.PI / 2;
        break;
    case "bawah":
        angle = Math.PI;
        break;
    case "kiri_atas":
        angle = Math.toRadians(-45);
        break;
    case "kanan_atas":
        angle = Math.toRadians(45);
        break;
    case "kiri_bawah":
        angle = Math.toRadians(-135);
        break;
    case "kanan_bawah":
        angle = Math.toRadians(135);
        break;
    default:
        angle = 0;
        break;
}

                AffineTransform transform = new AffineTransform();
                transform.translate(x + width / 2.0, y + height / 2.0);
                transform.rotate(angle);
                transform.translate(-width / 2.0, -height / 2.0);

                g2d.drawImage(scaledSprite, transform, null);
                g2d.dispose();
            }
        }
    }

    private boolean isWalkable(int px, int py, BufferedImage map, int panelW, int panelH) {
        // If map isn't provided, return false to be safe
        if (map == null) {
            return false; 
        }
        
        int imgW = map.getWidth();
        int imgH = map.getHeight();
        int scaledX = px * imgW / panelW;
        int scaledY = py * imgH / panelH;

        if (scaledX < 0 || scaledY < 0 || scaledX >= imgW || scaledY >= imgH) {
            return false;
        }

        Color color = new Color(map.getRGB(scaledX, scaledY));
        
        // Kriteria untuk merah muda (233, 73, 75):
        if (color.getRed() > 220 && color.getGreen() < 80 && color.getBlue() < 80) {
            if (hasExtraItem() && !hasFinished) { // <-- tambahkan !hasFinished
                hasFinished = true;               // <-- set sudah finish
                System.out.println("Mantap");
                if (onFinish != null) onFinish.run();
            }
            return false;
        }

        // Allow walking on white areas (RGB all > 200)
        boolean isWhiteArea = color.getRed() > 200 && color.getGreen() > 200 && color.getBlue() > 200;
        
        // Allow walking on green areas (high green, low red and blue)
        boolean isGreenArea = color.getGreen() > 200 && color.getRed() < 100 && color.getBlue() < 100;
        
        // Return true if either white area or green area
        return isWhiteArea || isGreenArea;
    }

    // Check if player is in green hiding area
    private boolean isInHidingArea(int px, int py, BufferedImage map, int panelW, int panelH) {
        // If map isn't provided, return false to be safe
        if (map == null) {
            return false;
        }
        
        int imgW = map.getWidth();
        int imgH = map.getHeight();
        int scaledX = px * imgW / panelW;
        int scaledY = py * imgH / panelH;

        if (scaledX < 0 || scaledY < 0 || scaledX >= imgW || scaledY >= imgH) {
            return false;
        }

        Color color = new Color(map.getRGB(scaledX, scaledY));
        
        // Detect green area (high green, low red and blue)
        return color.getGreen() > 200 && color.getRed() < 100 && color.getBlue() < 100;
    }


    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getGrabAbilityLevel() {
        return grabAbilityLevel;
    }

    public float getMoveSpeedNormal() {
        return moveSpeedNormal;
    }

    public float getMoveSpeedFast() {
        return moveSpeedFast;
    }

    public Ellipse2D getDetectionCircle() {
        int baseRadius = 40;
        int adjustedRadius = baseRadius + (grabAbilityLevel - 1) * 10;
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        return new Ellipse2D.Double(centerX - adjustedRadius, centerY - adjustedRadius, adjustedRadius * 2, adjustedRadius * 2);
    }

    public void drawDetectionArea(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        int radius = 70;
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int diameter = radius * 2;

        g2d.setColor(new Color(255, 255, 0, 100)); // Kuning transparan
        g2d.fillOval(centerX - radius, centerY - radius, diameter, diameter);
        g2d.dispose();
    }

    public void drawVisibilityOverlay(Graphics g, int panelWidth, int panelHeight) {
        // Don't draw visibility overlay when hiding
        if (isHiding) {
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g.create();

        // Enable anti-aliasing for smoother circle
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Create the overlay with a circular hole
        Area overlay = new Area(new Rectangle(0, 0, panelWidth, panelHeight));

        // Player center coordinates
        int playerCenterX = x + width / 2;
        int playerCenterY = y + height / 2;

        // Create circle area to subtract from overlay
        Area circle = new Area(new Ellipse2D.Double(
            playerCenterX - visibilityRadius, 
            playerCenterY - visibilityRadius, 
            visibilityRadius * 2, 
            visibilityRadius * 2
        ));

        // Subtract the circle from the overlay to create transparency
        overlay.subtract(circle);

        // Set semi-transparent black color and draw the overlay
        g2d.setColor(new Color(0, 0, 0, 120)); // Semi-transparent black
        g2d.fill(overlay);

        g2d.dispose();
    }

    public void setHasExtraItem(boolean status) {
        this.hasExtraItem = status;
    }

    public boolean hasExtraItem() {
        return hasExtraItem;
    }
    
    public boolean isHiding() {
        return isHiding;
    }
    
    public void setHiding(boolean hiding) {
        if (this.isHiding != hiding) {
            this.isHiding = hiding;
            
            // Start smoke animation when toggling hiding state
            smokeIndex = 0;
            isPlayingSmoke = true;
            if (!smokeTimer.isRunning()) {
                smokeTimer.start();
            }
        }
    }
    
    public boolean isInHidingArea() {
        return inHidingArea;
    }

    public void setPendingGold(int gold) {
        this.pendingGold = gold;
    }

    public int getPendingGold() {
        return this.pendingGold;
    }

    public boolean hasPendingGold() {
        return this.pendingGold > 0;
    }

    public void clearPendingGold() {
        this.pendingGold = 0;
    }

    public void addPendingGold(int value) {
        this.pendingGold += value;
    }

    public void resetPendingGold() {
        this.pendingGold = 0;
    }

    public void increaseMaxStamina(float amount) {
        this.maxStamina += amount;
        this.currentStamina = this.maxStamina;
    }

    public void increaseSpeed(float amount) {
        this.moveSpeedNormal += amount;
        this.moveSpeedFast += amount;
    }

    public void increaseGrabAbility(int amount) {
        this.grabAbilityLevel += amount;
        this.grabSpeed += 0.2f * amount;
    }

    public void setArah(String arah) {
        this.arah = arah;
    }

    public boolean hasRottenDonut() {
        return rottenDonutCount > 0;
    }

    public void useRottenDonut() {
        if (rottenDonutCount <= 0) return;
        rottenDonutCount--;
    }
    
    public void useInvisibilityPotion() {
        if (invisibilityPotionCount <= 0 || isInvisible) return;
        isInvisible = true;
        invisibilityPotionCount--;
        if (invisibilityTimer != null) {
            invisibilityTimer.stop();
        }
        invisibilityTimer = new Timer(invisibilityDuration, e -> {
            isInvisible = false;
            ((Timer)e.getSource()).stop();
        });
        invisibilityTimer.setRepeats(false);
        invisibilityTimer.start();
        smokeIndex = 0;
        isPlayingSmoke = true;
        if (!smokeTimer.isRunning()) {
            smokeTimer.start();
        }
    }
    
    public boolean isInvisible() {
        return isInvisible;
    }
    
    public void setHasRottenDonut(boolean hasDonut) {
        if (hasDonut) {
            rottenDonutCount++;
        }
    }
    
    public void setHasInvisibilityPotion(boolean hasPotion) {
        if (hasPotion) {
            invisibilityPotionCount++;
        }
    }
    
    // Add methods to get the current counts
    public int getRottenDonutCount() {
        return rottenDonutCount;
    }
    
    public int getInvisibilityPotionCount() {
        return invisibilityPotionCount;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.xPos = x;
        this.yPos = y;
    }
}
