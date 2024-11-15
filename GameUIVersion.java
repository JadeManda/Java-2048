import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class GameUIVersion extends JFrame implements KeyListener {

    //主要是整个UI的组织结构没设计好，东拼西凑。

    private JLabel[][] tilesLabel;
    private int[][] tiles;
    private boolean isMove,isMerge;
    private boolean isGameWon = false;
    private JLabel scoreLabel, bestLabel;
    private int score,bestScore;
    private final Map<Integer, Color> colorMap = new HashMap<>();
    private final int LEFT = 4, RIGHT = 6, UP = 8, DOWN = 2;
    private JPanel gridPanel;
    private JPanel overlayPanel;


    public GameUIVersion() {
        // Frame基本设置
        initFrame();
        // 初始化颜色映射
        initColorMap();

        initInfo();

        initGrid();

        initOverlayPanel();

        initJLayerPane();


        //初始化Model
        initModel();

        // 将组件添加到窗口

        this.addKeyListener(this);

        setFocusable(true);
        requestFocusInWindow();

        setVisible(true);
    }

    private void initFrame() {
        setSize(690, 930);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setBackground(new Color(250, 248, 238));

    }

    private void initInfo() {
        // info设置
        //在BorderLayout布局管理器中直接设置setSize通常没有效果，因此将边框设置到gridPanel上
        JPanel infoPanel = new JPanel(new GridLayout(2, 2));
        infoPanel.setPreferredSize(new Dimension(646, 240)); // 设置推荐大小
        infoPanel.setBorder(new EmptyBorder(30, 30, 30, 30)); // 设置边距
        infoPanel.setBackground(new Color(250,248,238));

        JPanel scores = new JPanel(new GridLayout(2, 1));
        scoreLabel=new JLabel("Score:"+0);
        scores.setBackground(new Color(250,248,238));
        bestLabel=new JLabel("Best: "+bestScore);
        scoreLabel.setFont(new Font("Arial",Font.BOLD,30));
        bestLabel.setFont(new Font("Arial",Font.BOLD,30));
        scores.add(scoreLabel);
        scores.add(bestLabel);


        JButton restart = new JButton("New Game");
        restart.setFont(new Font("Arial",Font.BOLD,50));
        restart.setHorizontalAlignment(SwingConstants.CENTER);
        restart.setVerticalAlignment(SwingConstants.CENTER);
        restart.addActionListener(e->restartGame());
        restart.setBackground(new Color(146,121,99));


        JButton quit = new JButton("Quit");
        quit.setFont(new Font("Arial",Font.BOLD,50));
        quit.setHorizontalAlignment(SwingConstants.CENTER);
        quit.setVerticalAlignment(SwingConstants.CENTER);
        quit.addActionListener(e->System.exit(0));
        quit.setBackground(new Color(146,121,99));

        Label title = new Label("2048");
        title.setFont(new Font("Arial", Font.BOLD, 70));
        title.setAlignment(Label.CENTER);

        infoPanel.add(title);
        infoPanel.add(scores);
        infoPanel.add(restart);
        infoPanel.add(quit);
        this.add(infoPanel, BorderLayout.NORTH);
    }

    private void initJLayerPane(){
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(gridPanel.getPreferredSize());

        gridPanel.setBounds(18, 0, gridPanel.getPreferredSize().width, gridPanel.getPreferredSize().height);
        overlayPanel.setBounds(18, 0, gridPanel.getPreferredSize().width, gridPanel.getPreferredSize().height);
        layeredPane.setBorder(new LineBorder(new Color(250,248,238),30));

        layeredPane.add(gridPanel, JLayeredPane.DEFAULT_LAYER);   // 基本层
        layeredPane.add(overlayPanel, JLayeredPane.PALETTE_LAYER); // 覆盖层

        this.add(layeredPane, BorderLayout.CENTER);
    }

    private void initGrid() {
        //创建输和赢的结算界面的panel,使用覆盖方法

        // 4*4 网格设置
        gridPanel = new JPanel(new GridLayout(4, 4, 10, 10)); // 4x4 网格，格子间距为 10
        gridPanel.setBackground(new Color(189, 173, 158)); // 深色背景

        // 设置双层边框：外边的白色边框和内边的空白边框
        Border outerBorder = new LineBorder(new Color(250, 248, 238), 14); // 外边框
        Border innerBorder = new EmptyBorder(10, 10, 10, 10); // 内边框
        gridPanel.setBorder(new CompoundBorder(outerBorder, innerBorder));


        tilesLabel = new JLabel[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                tilesLabel[row][col] = createTile();
                gridPanel.add(tilesLabel[row][col]);
            }
        }
    }

    private void initOverlayPanel(){
        overlayPanel=new JPanel();
        overlayPanel.setBackground(new Color(0,0,0,180));
        overlayPanel.setLayout(new GridBagLayout());
        overlayPanel.setVisible(false);
        overlayPanel.setOpaque(false);

        JLabel messageLabel=new JLabel();
        messageLabel.setFont(new Font("Arial",Font.BOLD,36));
        overlayPanel.add(messageLabel);
    }

    private void showWinOverlay(){
        ((JLabel)overlayPanel.getComponent(0)).setText("You Win!");
        overlayPanel.setVisible(true);
    }

    private void showLoseOverlay() {
        ((JLabel) overlayPanel.getComponent(0)).setText("You Lose!");
        overlayPanel.setVisible(true);
    }

    private void initColorMap() {
        colorMap.put(2, new Color(238, 228, 218));
        colorMap.put(4, new Color(237, 224, 200));
        colorMap.put(8, new Color(242, 177, 121));
        colorMap.put(16, new Color(245, 149, 99));
        colorMap.put(32, new Color(246, 124, 95));
        colorMap.put(64, new Color(246, 94, 59));
        colorMap.put(128, new Color(237, 207, 114));
        colorMap.put(256, new Color(237, 204, 97));
        colorMap.put(512, new Color(237, 200, 80));
        colorMap.put(1024, new Color(237, 197, 63));
        colorMap.put(2048, new Color(237, 194, 46));
    }

    public JLabel createTile() {
        JLabel tile = new JLabel("", SwingConstants.CENTER);
        tile.setFont(new Font("Arial", Font.BOLD, 70));
        //不要通过setsize设置，对于布局管理器，更倾向使用setPreferredSize
        tile.setOpaque(true);
        tile.setBackground(new Color(204, 192, 179)); // 默认空格颜色
        tile.setPreferredSize(new Dimension(140, 140)); // 设置格子大小
        return tile;
    }

    public void updateTile(int row,int col,int val){
        JLabel tile = tilesLabel[row][col];
        if(val==0){
            tile.setText("");
            tile.setBackground(new Color(204, 192, 179));
        }else{
            tile.setText(String.valueOf(val));
            tile.setBackground(colorMap.get(val));
        }
    }

    private void initModel() {
        //生成两个随即砖块
        this.tiles=new int[4][4];
        int row1=random(0, 3);
        int col1=random(0, 3);
        int row2=random(0, 3);
        int col2=random(0, 3);
        int num1=random(0, 1) == 0 ? 2 : 4;
        int num2=random(0, 1) == 0 ? 2 : 4;
        tiles[row1][col1]=num1;
        tiles[row2][col2]=num2;
        updateTile(row1,col1,num1);
        updateTile(row2,col2,num2);
        score=0;
    }

    private int random(int s, int e) {
        return (int) Math.round(Math.random() * (e - s) + s);
    }

    private void merge(int flag) {
        isMerge=false;
        if (flag == LEFT) {
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 4; col++) {
                    if (tiles[row][col] == 0) {
                        continue;
                    }
                    for (int j = col + 1; j < 4; j++) {
                        if (tiles[row][j] != tiles[row][col] && tiles[row][j] != 0) {
                            break;
                        }
                        if (tiles[row][j] == tiles[row][col]) {
                            tiles[row][col] += tiles[row][j];
                            score+=tiles[row][col];
                            tiles[row][j] = 0;
                            isMerge=true;
                            break;
                        }
                    }
                }
            }
        } else if (flag == RIGHT) {
            for (int row = 0; row < 4; row++) {
                for (int col = 3; col >= 0; col--) {
                    if (tiles[row][col] == 0) {
                        continue;
                    }
                    for (int j = col - 1; j >= 0; j--) {
                        if (tiles[row][j] != tiles[row][col] && tiles[row][j] != 0) {
                            break;
                        }
                        if (tiles[row][j] == tiles[row][col]) {
                            tiles[row][col] += tiles[row][j];
                            score+=tiles[row][col];
                            tiles[row][j] = 0;
                            isMerge=true;
                            break;
                        }
                    }
                }
            }
        } else if (flag == UP) {
            for (int col = 0; col < 4; col++) {
                for (int row = 0; row < 4; row++) {
                    if (tiles[row][col] == 0) {
                        continue;
                    }
                    for (int j = row + 1; j < 4; j++) {
                        if (tiles[j][col] != tiles[row][col] && tiles[j][col] != 0) {
                            break;
                        }
                        if (tiles[j][col] == tiles[row][col]) {
                            tiles[row][col] += tiles[j][col];
                            score+=tiles[row][col];
                            tiles[j][col] = 0;
                            isMerge=true;
                            break;
                        }
                    }
                }
            }
        } else if (flag == DOWN) {
            for (int col = 0; col < 4; col++) {
                for (int row = 3; row >= 0; row--) {
                    if (tiles[row][col] == 0) {
                        continue;
                    }
                    for (int j = row - 1; j >= 0; j--) {
                        if (tiles[j][col] != tiles[row][col] && tiles[j][col] != 0) {
                            break;
                        }
                        if (tiles[j][col] == tiles[row][col]) {
                            tiles[row][col] += tiles[j][col];
                            //记分
                            score+=tiles[row][col];
                            tiles[j][col] = 0;
                            isMerge=true;
                            break;
                        }
                    }
                }
            }
        }
    }

    private void move(int flag) {
        isMove=false;
        int[][] tmp = new int[4][4];
        if (flag == UP) {
            //这里是整理一行，拷贝一行，但是判断move是从整个矩阵来说的，所以不好改，只能最后再重新来一次
            for (int col = 0; col < 4; col++) {
                for (int row = 0, j = 0; row < 4; row++) {
                    if (tiles[row][col] != 0) {
                        tmp[j++][col] = tiles[row][col];
                    }
                }
            }
            isMove=isMove(tmp);
            if(isMove){
                for(int row=0;row<4;row++){
                    System.arraycopy(tmp[row], 0, tiles[row], 0, 4);
                }
            }
        } else if (flag == DOWN) {
            for (int col = 0; col < 4; col++) {
                for (int row = 3, j = 3; row >= 0; row--) {
                    if (tiles[row][col] != 0) {
                        tmp[j--][col] = tiles[row][col];
                    }
                }
            }
            isMove=isMove(tmp);
            if(isMove){
                for(int row=0;row<4;row++){
                    System.arraycopy(tmp[row], 0, tiles[row], 0, 4);
                }
            }

        } else if (flag == LEFT) {
            for (int row = 0; row < 4; row++) {
                for (int col = 0, j = 0; col < 4; col++) {
                    if (tiles[row][col] != 0) {
                        //row写成col了，bugfix
                        tmp[row][j++] = tiles[row][col];
                    }
                }
            }
            isMove=isMove(tmp);
            if(isMove){
                for(int row=0;row<4;row++){
                    System.arraycopy(tmp[row], 0, tiles[row], 0, 4);
                }
            }
        } else if (flag == RIGHT) {
            //生成移动后的数组
            for (int row = 0; row < 4; row++) {
                for (int col = 3, j = 3; col >= 0; col--) {
                    if (tiles[row][col] != 0) {
                        tmp[row][j--] = tiles[row][col];
                    }
                }
            }
            //比对tmp和tiles，判断是否有移动，如果相等，那就没移动
            isMove=isMove(tmp);
            //如果移动了，那么再进行拷贝
            if(isMove){
                for(int row=0;row<4;row++){
                    System.arraycopy(tmp[row], 0, tiles[row], 0, 4);
                }
            }
        }
    }

    private boolean isLose() {
        //扫描tiles，只要发现一个0，那就没输，返回false，对于每一个位置，分别扫描它的上下左右，只要发现一个和
        //该位置的数相同的，同样没输，返回false，这一步在扫描0之后，最后返回true
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (tiles[row][col] == 0) {
                    return false;
                }
            }
        }
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int left, right, up, down, cur;
                left = col == 0 ? -1 : tiles[row][col - 1];
                right = col == 3 ? -1 : tiles[row][col + 1];
                up = row == 0 ? -1 : tiles[row - 1][col];
                down = row == 3 ? -1 : tiles[row + 1][col];
                cur = tiles[row][col];
                if (cur == left || cur == right || cur == up || cur == down) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isWin() {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (tiles[row][col] == 2048) {
                    return true;
                }
            }
        }
        return false;
    }

    private void giveTiles() {
        int[][] zeroIndex = new int[16][2];
        int count = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (tiles[row][col] == 0) {
                    zeroIndex[count][0] = row;
                    zeroIndex[count][1] = col;
                    count++;
                }
            }
        }
        //bugfix,round四舍五入导致的越界访问到0,不能四舍五入，应该直接截断
        int pos = (int) (Math.random() * count);
        int num = (int) (Math.random()*10)<=8 ? 2 : 4;
        tiles[zeroIndex[pos][0]][zeroIndex[pos][1]] = num;
    }

    private void refresh(){
        for(int row=0;row<4;row++){
            for(int col=0;col<4;col++){
                updateTile(row,col,tiles[row][col]);
            }
        }
    }

    public static void main(String[] args) {
        new GameUIVersion();
    }

    private void restartGame() {
        overlayPanel.setVisible(false);
        isGameWon=false;
        bestScore=Math.max(bestScore,score);
        bestLabel.setText("Best: "+bestScore);
        initModel();  // 再次初始化两个数字
        score = 0;
        scoreLabel.setText("Score:"+score);
        setFocusable(true);
        requestFocusInWindow();
        refresh();  // 刷新显示
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(isGameWon){
            return;
        }
        switch (e.getKeyCode()){
            case KeyEvent.VK_UP -> {
                merge(UP);
                move(UP);
                scoreLabel.setText("Score:"+score);
                if(isMove||isMerge){
                    giveTiles();
                }
            }
            case KeyEvent.VK_DOWN -> {
                merge(DOWN);
                move(DOWN);
                scoreLabel.setText("Score:"+score);
                if (isMove||isMerge) {
                    giveTiles();
                }
            }
            case KeyEvent.VK_LEFT -> {
                merge(LEFT);
                move(LEFT);
                scoreLabel.setText("Score:"+score);
                if (isMove||isMerge) {
                    giveTiles();
                }
            }
            case KeyEvent.VK_RIGHT -> {
                merge(RIGHT);
                move(RIGHT);
                scoreLabel.setText("Score:"+score);
                if (isMove||isMerge) {
                    giveTiles();
                }
            }
        }
        refresh();
        if(isWin()){
            showWinOverlay();
            isGameWon=true;
        }
        if(isLose()){
            showLoseOverlay();
        }
    }

    private boolean isMove(int[][] tmp) {
        for(int row=0;row<4;row++){
            for(int col=0;col<4;col++){
                if(tmp[row][col]!=tiles[row][col]){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
