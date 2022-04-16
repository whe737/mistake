import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
public class Panel extends JPanel
{
    private Frame parentFrame;
    final int WIDTH = 1280;
    final int HEIGHT = 720;
    Thread game;

    Sprite player = new Sprite(50, 300, 50, 60, 0, false);
    ArrayList<Sprite> obstacles = new ArrayList<>();
    ArrayList<Sprite> enemies = new ArrayList<>();
    public Panel(Frame parentFrame)
    {
        this.parentFrame = parentFrame;
        this.setLayout(null);
        this.setBounds(0, 0, WIDTH, HEIGHT);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT)); // used with frame.pack()

        obstacles.add(new Sprite(0, 0, 50, 50, 0, false));
        for (int i =0; i < 4; i++)
        {
            enemies.add(new Sprite(WIDTH, (int)(Math.random() * (HEIGHT - 100) + 50), 50, 50, -(int)(Math.random() *3 + 2), false));
        }
        

        game = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true)
                {
                    // bullets
                    for (int i = 0; i < enemies.size(); i++)
                    {
                        enemies.get(i).moveHorizontally();
                        if (enemies.get(i).x() < 0){
                            enemies.remove(i);
                            i--;
                            enemies.add(new Sprite(WIDTH, (int) (Math.random() * (HEIGHT - 100) + 50), 50, 50, -(int)(Math.random() *3 + 2), false));
                        }
                    }

                    player.moveHorizontally();
                    if (player.x() < 0) player.setX(0);
                    else if (player.x() > WIDTH/4) player.setX(WIDTH/4);
                    // object collision x axis
                    ArrayList<Sprite> collidesWith = player.collidesWith(obstacles);
                    if (collidesWith.size() > 0)
                    {
                        if (player.xVelocity() < 0) // LEFT
                        {
                            player.setX(collidesWith.get(0).getRight());
                        }     
                        else if (player.xVelocity() > 0) // RIGHT
                        {
                            player.setX(collidesWith.get(0).getLeft() - player.getWidth());
                        }
                    }
                    player.setXVelocity(0);

                    player.moveVertically();
                    if (player.y() < 0) player.setY(0);
                    else if (player.y() +player.getHeight() > HEIGHT) player.setY(HEIGHT-player.getHeight());
                    // object collision x axis
                    collidesWith = player.collidesWith(obstacles);
                    if (collidesWith.size() > 0)
                    {
                        if (player.yVelocity() < 0) // DOWN
                        {
                            player.setY(collidesWith.get(0).getBottom());
                        }     
                        else if (player.yVelocity() > 0) // UP
                        {
                            player.setY(collidesWith.get(0).getTop() - player.getHeight());
                        }
                    }
                    player.setYVelocity(0);

                    // enemy collision
                    collidesWith = player.collidesWith(enemies);
                    if (collidesWith.size() > 0)
                    {
                        Sprite enemy = collidesWith.get(0);
                        Sprite.health -= 20;
                        System.out.println("Player got hit by " + enemy + "Player Health: " + Sprite.health);
                        if (Sprite.health <= 0)
                        {
                            System.out.println("Player dead");
                            endGame();
                        }
                        for (int i = 0; i < enemies.size(); i++)
                        {
                            if(enemies.get(i) == enemy) 
                            {
                                enemies.remove(i);
                                enemies.add(new Sprite(WIDTH, (int) (Math.random() * (HEIGHT - 100) + 50), 50, 50, -(int)(Math.random() *3 + 2), false));
                            }
                        }
                    }

                    repaint();
                    sleep(5);
                }
                
            }
            
        });
        game.start();
    }

    public void paint(Graphics g)
    {
        super.paint(g); // paint background
		Graphics2D g2D = (Graphics2D) g;
        g2D.setBackground(Color.black);

        player.draw(g2D);

        g2D.setColor(Color.blue);
        for (Sprite s: obstacles) s.draw(g2D);

        g2D.setColor(Color.red);
        for (Sprite s: enemies) s.draw(g2D);
    }

    public void sleep(int mili)
    {
        try {
            Thread.sleep(mili);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void endGame()
    {
        System.out.println("GAME OVER");
        parentFrame.panel.setVisible(false);
        parentFrame.add(new EndScreen());
        game.stop();
    }
}