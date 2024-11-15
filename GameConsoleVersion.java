import java.util.Scanner;

public class GameConsoleVersion {
    private final int[][] tiles = new int[4][4];
    private final int LEFT = 4, RIGHT = 6, UP = 8, DOWN = 2;
    private boolean isWin = false;
    private boolean isLose = false;
    //TODO：
    //1.实现合并移动，使用8，2，4，6进行上下左右的移动命令
    //2.实现每次移动过后都在剩下的0位置随机生成一个2或4
    //3.实现判断输的方法
    //4.实现判断赢的方法

    public GameConsoleVersion() {
        init();
        start();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        print();
        while (!isLose) {
            int flag = scanner.nextInt();
            perform(flag);
            print();
            if(isWin){
                System.out.println("You Win!");
                return;
            }
        }
        System.out.println("You Lose!");
    }

    private void init() {
        //生成两个随即砖块
        tiles[random(0, 3)][random(0, 3)] = random(0, 1) == 0 ? 2 : 4;
        tiles[random(0, 3)][random(0, 3)] = random(0, 1) == 0 ? 2 : 4;
    }

    private int random(int s, int e) {
        return (int) Math.round(Math.random() * (e - s) + s);
    }

    private void perform(int flag) {
        merge(flag);
        move(flag);
        isWin = isWin();
        isLose = isLose();
        giveTiles();
    }


    private void merge(int flag) {
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
                            tiles[row][j] = 0;
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
                            tiles[row][j] = 0;
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
                            tiles[j][col] = 0;
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
                            tiles[j][col] = 0;
                            break;
                        }
                    }
                }
            }
        }
    }

    private void move(int flag) {
        int[][] tmp = new int[4][4];
        if (flag == UP) {
            for (int col = 0; col < 4; col++) {
                for (int row = 0, j = 0; row < 4; row++) {
                    if (tiles[row][col] != 0) {
                        tmp[j++][col] = tiles[row][col];
                    }
                }
                for (int row = 0; row < 4; row++) {
                    if (tmp[row][col] != 0) {
                        tiles[row][col] = tmp[row][col];
                    } else {
                        tiles[row][col] = 0;
                    }
                }
            }
        } else if (flag == DOWN) {
            for (int col = 0; col < 4; col++) {
                for (int row = 3, j = 3; row >= 0; row--) {
                    if (tiles[row][col] != 0) {
                        tmp[j--][col] = tiles[row][col];
                    }
                }
                for (int row = 3; row >= 0; row--) {
                    if (tmp[row][col] != 0) {
                        tiles[row][col] = tmp[row][col];
                    } else {
                        tiles[row][col] = 0;
                    }
                }
            }
        } else if (flag == LEFT) {
            for (int row = 0; row < 4; row++) {
                for (int col = 0, j = 0; col < 4; col++) {
                    if (tiles[row][col] != 0) {
                        tmp[row][j++] = tiles[row][col];
                    }
                }
                for (int col = 0; col < 4; col++) {
                    if (tmp[row][col] != 0) {
                        tiles[row][col] = tmp[row][col];
                    } else {
                        tiles[row][col] = 0;
                    }
                }
            }
        } else if (flag == RIGHT) {
            for (int row = 0; row < 4; row++) {
                for (int col = 3, j = 3; col >= 0; col--) {
                    if (tiles[row][col] != 0) {
                        tmp[row][j--] = tiles[row][col];
                    }
                }
                for (int col = 3; col >= 0; col--) {
                    if (tmp[row][col] != 0) {
                        tiles[row][col] = tmp[row][col];
                    } else {
                        tiles[row][col] = 0;
                    }
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
        int pos = (int) Math.round(Math.random() * count);
        int num = (int) Math.round(Math.random()) == 0 ? 2 : 4;
        tiles[zeroIndex[pos][0]][zeroIndex[pos][1]] = num;
    }

    public void print() {
        for (int[] tile : tiles) {
            for(int i=0;i<4;i++){
                System.out.print(tile[i]+"  ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        new GameConsoleVersion();
    }
}
