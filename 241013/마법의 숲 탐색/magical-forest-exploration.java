/*
문제
R행 C열의 마법의 숲 탐색.
가장 위를 1행 가장 아래를 R행
동, 서, 남쪽은 마법의 벽으로 막혀있고, 북쪽을 통해서만 숲에 들어올 수 있다.
K 명의 정령은 각자 골렘을 타고 숲을 탐색
각 골렘은 십자 모양의 구조를 가지고 있고 중앙을 포함해 총 5칸이다.
중앙을 제외한 4칸 중 한칸은 골렘의 출구이다.
정령은 어떤 방향이든 골렘 탑승 가능, 내릴 때에는 정해진 출구로만 가능
i번 째 탐색하는 골렘은 숲의 가장 북쪽에서 시작해 중앙이 ci열이 되도록 하는 위치에서
내려고이 시작.
초기 골렘의 출구는 di 방향에 위치해 있다.
골렘이 움직이지 못할 때 까지 해당 과정을 반복
1. 남쪽으로 한 칸 내려감 (밑에 걸리는게 없어야 한다.)
2. 1이 불가능하면 서쪽으로 이동하여 다시 1 반복. (출구가 반시계 방향으로 바뀜)
3. 2도 불가능하면 동쪽으로 이동하여 다시 1 반복. (출구가 시계 방향으로 바뀜)
4. 1, 2, 3 모두 불가능할 때 or 가장 밑에 도달하면 정령이 움직이기 시작.
5. 

풀이
골렘의 위치를 나타내는 골렘 맵을 만들어서 관리한다.
=> 골렘 맵은 0이면 골렘이 없는 길, 아니면 최종 골렘의 값으로 채워진다.
bfs를 통해 연결된 골렘을 모두 탐색하여 정령이 갈 수 있는 가장 아래의 행을 구한다.
현재 골렘의 중심부에서 시작하여 상하좌우 탐색.
각 탐색에 대한 조건은 아래와 같다.
1. 바깥으로 벗어나지 않게
2. 현재 나와 같은 골렘이 아니면 출구일 때만 넘어갈 수 있다.
=> 출구 관리는 어떻게 할 것인가?
골렘의 이동이 끝나면 출구를 기록한다. 출구만 기록한 2차원 배열을 만든다.

*/

import java.io.*;
import java.util.*;

public class Main {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
    static StringTokenizer stn;
    static int[] dr = {-1, 0, 1, 0};
    static int[] dc = {0, 1, 0, -1};
    static int ans;
    static int R, C, K, golemStart, golemExit, nextRow, nextCol;
    static int centerRow, centerCol;
    static boolean[][] isExit, isVisited;
    static int[][] golemMap;

    public static void main(String[] args) throws Exception {
        stn = new StringTokenizer(br.readLine().trim());

        R = Integer.parseInt(stn.nextToken());
        C = Integer.parseInt(stn.nextToken());
        K = Integer.parseInt(stn.nextToken());

        isExit = new boolean[R + 1][C + 1];
        golemMap = new int[R + 1][C + 1];
        for (int i = 1; i <= K; i++) {
            stn = new StringTokenizer(br.readLine().trim());
            golemStart = Integer.parseInt(stn.nextToken());
            golemExit = Integer.parseInt(stn.nextToken());

            simulate(i);
        }

        bw.write(Long.toString(ans));
        bw.flush();
    }

    public static void simulate(int i) {
        // 골렘의 start를 바탕으로 골렘을 이동시킨다.
        centerRow = -1;
        centerCol = golemStart;
        while(move());

        if (centerRow < 2) {
            resetMap();
            return;
        }
        setMap(i);

        // 골렘의 이동이 끝난 후 map을 바탕으로 정령을 이동시킨다.
        int temp = bfs(centerRow, centerCol);
        // System.out.println(i + " 번째 정령의 위치는 " + temp + " 번이다.");
        ans += temp;

        // for (int a = 1; a <= R; a++) {
        //     System.out.println("");
        //     for (int b = 1; b <= C; b++) {
        //         System.out.print(golemMap[a][b] + " ");
        //     }
        // }
    }

    public static void setMap(int i) {
        golemMap[centerRow][centerCol] = i;
        golemMap[centerRow][centerCol - 1] = i;
        golemMap[centerRow][centerCol + 1] = i;
        golemMap[centerRow - 1][centerCol] = i;
        golemMap[centerRow + 1][centerCol] = i;
        // System.out.println(i + "=> i " + "golemExit : "  + golemExit);

        isExit[centerRow + dr[golemExit]][centerCol + dc[golemExit]] = true;
    }

    public static boolean move() {
        if (centerRow == R - 1) {
            return false;
        }
        // 중앙 row, col 값을 얻었다.
        // 중앙 값을 기준으로 아래로 갈 수 있는지 확인
        // 아래로 갈 수 있다면 이동하고 true 리턴
        // 아래로 갈 수 없다면 서, 동쪽을 고려하고 true 리턴
        // 아래, 서, 동 모두 갈 수 없다면 false 리턴.
        if (canSouth(centerRow, centerCol)) {
            centerRow++;
            return true;
        }
        if (canWest()) {
            centerRow++;
            centerCol--;
            changeDirection(false);
            return true;
        }
        if (canEast()) {
            centerRow++;
            centerCol++;
            changeDirection(true);
            return true;
        }
        return false;
    }

    public static void changeDirection(boolean isClockWise) {
        if (isClockWise) {
            golemExit = (golemExit + 1) % 4;
            return;
        }
        if(golemExit == 0) {
            golemExit = 3;
            return;
        }
        golemExit = golemExit - 1;
    }

    public static boolean canEast() {
        int a, b, c;
        a = centerRow - 1;
        b = centerRow;
        c = centerRow + 1;

        if (centerCol >= C - 1) {
            return false;
        }
        if (b <= 0) {
            if (golemMap[c][centerCol - 1] == 0) {
                if(canSouth(centerRow, centerCol + 1)) {
                    return true;
                }
                return false;
            }
        }
        if (golemMap[a][centerCol + 1] == 0 &&
        golemMap[b][centerCol + 2] == 0 &&
        golemMap[c][centerCol + 1] == 0) {
            if (canSouth(centerRow, centerCol + 1)) {
                return true;
            }
            return false;
        }
        return false;
    }

    public static boolean canWest() {
        int a, b, c;
        a = centerRow - 1;
        b = centerRow;
        c = centerRow + 1;

        if (centerCol <= 2) {
            return false;
        }
        if (b <= 0) {
            if (golemMap[c][centerCol - 1] == 0) {
                if(canSouth(centerRow, centerCol - 1)) {
                    return true;
                }
                return false;
            }
        }
        if (golemMap[a][centerCol - 1] == 0 &&
        golemMap[b][centerCol - 2] == 0 &&
        golemMap[c][centerCol - 1] == 0) {
            if (canSouth(centerRow, centerCol - 1)) {
                return true;
            }
            return false;
        }
        return false;
    }

    public static boolean canSouth(int centerRow, int centerCol) {
        int a, b, c;
        a = centerCol - 1;
        b = centerCol;
        c = centerCol + 1;

        if (golemMap[centerRow + 1][a] == 0 &&
        golemMap[centerRow + 2][b] == 0 &&
        golemMap[centerRow + 1][c] == 0) {
            return true;
        }
        return false;
    }

    public static void resetMap() {
        golemMap = new int[R + 1][C + 1];
        isExit = new boolean[R + 1][C + 1];
    }

    public static int bfs(int startRow, int startCol) {
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[] {startRow, startCol});

        isVisited = new boolean[R + 1][C + 1];
        isVisited[startRow][startCol] = true;
        int bottom = 0;
        while(!queue.isEmpty()) {
            int[] cur = queue.poll();
            int curGolem = golemMap[cur[0]][cur[1]];

            bottom = Math.max(bottom, cur[0]);
            if (bottom == R) {
                return bottom;
            }
            for (int i = 0; i < 4; i++) {
                nextRow = cur[0] + dr[i];
                nextCol = cur[1] + dc[i];

                if (nextRow < 1 || R < nextRow || nextCol < 1 || C < nextCol) {
                    continue;
                }
                if (isVisited[nextRow][nextCol]) {
                    continue;
                }
                int nextGolem = golemMap[nextRow][nextCol];
                if (nextGolem == 0) {
                    continue;
                }
                if (curGolem == nextGolem) {
                    queue.add(new int[] {nextRow, nextCol});
                    isVisited[nextRow][nextCol] = true;
                    continue;
                }
                if (!isExit[cur[0]][cur[1]]) {
                    continue;
                }
                queue.add(new int[] {nextRow, nextCol});
                isVisited[nextRow][nextCol] = true;
            }
        }
        return bottom;
    }
}

// 1. 바깥으로 벗어나지 않게
// 2. 현재 나와 같은 골렘이 아니면 출구일 때만 넘어갈 수 있다.
// => 출구 관리는 어떻게 할 것인가?