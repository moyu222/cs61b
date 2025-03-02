package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;


import java.util.*;

public class Room {

    public int length;
    public int height;
    public Position p;

    public Room(int length, int height, Position p) {
        this.length = length;
        this.height = height;
        this.p = p;
    }
    /**
     * generate random rooms in the world as many as possible
     */
    public static List<Room> genManyRoom(TETile[][] world, Random r, int WIDTH, int HEIGHT) {
        int failCnt = 0;
        List<Room> roomList = new ArrayList<>();

        while (failCnt != 10) {
            Position p = genRandomPosition(r,WIDTH, HEIGHT, roomList);
            int height = RandomUtils.uniform(r, 4, 9);
            int length = RandomUtils.uniform(r, 4, 9);
            if (canGen(world, p, length, height, WIDTH, HEIGHT)) {
                roomList.add(genRandomRoom(world, p, length, height));
                failCnt = 0;
            } else {
                failCnt += 1;
            }
        }
        return roomList;
    }


    /**
     * generate random size room at the specified position
     */
    private static Room genRandomRoom(TETile[][] world, Position p, int length, int height) {
        genWall(world, p, length, height);
        genFloor(world, p, length, height);
        // consider the room constructor
        return new Room(length, height, p);
    }

    /**
     * generate random Position p for room generation
     */
    private static Position genRandomPosition(Random r, int WIDTH, int HEIGHT, List<Room> roomList) {
        int minDistance = 2;
        int x = r.nextInt(WIDTH);
        int y = r.nextInt(HEIGHT);
        boolean validPosition = false;

        while (!validPosition) {
            x = r.nextInt(WIDTH);
            y = r.nextInt(HEIGHT);
            validPosition = true;

            // check the minimum distance between new room and existing rooms
            for (Room room: roomList) {
                if (Math.abs(x - room.p.x) < minDistance || Math.abs(y - room.p.y) < minDistance) {
                    validPosition = false;
                    break;
                }
            }
        }
        return new Position(x, y);
    }

    /**
     * decide if we can generate room at position p
     */
    private static boolean canGen(TETile[][] world, Position p, int length, int height, int WIDTH, int HEIGHT) {
        if (p.x + length > WIDTH || p.y - height + 1 < 0) {
            return false;
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                if (!world[p.x + j][p.y - i].description().equals(Tileset.NOTHING.description())) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * generate the wall of size l * w
     */
    private static void genWall(TETile[][] world, Position p, int l, int h) {
        for (int i = 0; i < l; i++) {

            world[p.x + i][p.y] = Tileset.WALL;
        }
        for (int i = 1; i < (h-1) ; i++) {
            world[p.x][p.y - i] = Tileset.WALL;
            world[p.x + l - 1][p.y - i] = Tileset.WALL;
        }
        for (int i = 0; i < l; i++) {
            world[p.x + i][p.y - (h-1)] = Tileset.WALL;
        }
    }

    /**
     * generate the floor of size l * w
     */
    private static void genFloor(TETile[][] world, Position p, int l, int h) {
        for (int i = 1; i < (h-1); i++){
            for (int j = 1; j < (l-1); j++) {
                world[p.x + j][p.y - i] = Tileset.FLOOR;
            }
        }
    }

    /**
     * Use Prim's Algorithm to connect all rooms with corridors
     */
    private static List<int[]> computeMST(List<Room> roomList) {
        int n = roomList.size();
        // priority Queue to store edges based on distance between rooms
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));

        // Add initial edges between rooms to the priority queue
        boolean[] inMST = new boolean[n];
        List<int[]> mst = new ArrayList<>();

        inMST[0] = true;
        for (int i = 1; i < n; i++) {
            int dist = manhattanDistance(roomList.get(0).p, roomList.get(i).p);
            pq.offer(new int[] {0, i, dist});
        }

        while (!pq.isEmpty() && mst.size() < n - 1) {
            int[] edge = pq.poll();
            int u = edge[0], v = edge[1], cost = edge[2];

            if (!inMST[v]) {
                // Mark room v as part of MST and generate a corridor
                inMST[v] = true;
                mst.add(edge);

                // Add new edges to the priority queue
                for (int i = 0; i < n; i++) {
                    if (!inMST[i]) {
                        int dist = manhattanDistance(roomList.get(v).p, roomList.get(i).p);
                        pq.offer(new int[]{v, i, dist});
                    }
                }
            }
        }
        return mst;
    }

    /**
     * manhattan distance between two position
     */
    private static int manhattanDistance(Position p1, Position p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }


    private Position getCenter() {
        int centerX = p.x + length / 2;
        int centerY = p.y - height / 2;
        return new Position(centerX,centerY);
    }

    public static void generateHallways(TETile[][] world, List<Room> roomList, List<int[]> connections, Random r) {
        for (int[] edge : connections) {
            Room roomA = roomList.get(edge[0]);
            Room roomB = roomList.get(edge[1]);

            Position centerA = roomA.getCenter();
            Position centerB = roomB.getCenter();

            // 先横向再纵向，或者先纵向再横向，形成 L 形走廊
            if (RandomUtils.uniform(r, 2) == 0) {
                // 先水平，再垂直
                createHorizontalHallway(world, centerA.x, centerB.x, centerA.y);
                createVerticalHallway(world, centerA.y, centerB.y, centerB.x);
                addCornerWall(world, centerB.x, centerA.y);
                addCornerWall(world, centerA.x, centerA.y);

            } else {
                // 先垂直，再水平
                createVerticalHallway(world, centerA.y, centerB.y, centerA.x);
                createHorizontalHallway(world, centerA.x, centerB.x, centerB.y);
                addCornerWall(world, centerA.x, centerB.y);
                addCornerWall(world, centerB.x, centerB.y);


            }
        }
    }

    /**
     * **添加 L 形拐角的墙壁**
     */
    private static void addCornerWall(TETile[][] world, int x, int y) {
        if (x >= 0 && x < world.length && y >= 0 && y < world[0].length
                && world[x][y].description().equals(Tileset.NOTHING.description())) {
            world[x][y] = Tileset.WALL;

        }
    }

    /**
     * 创建水平走廊
     */
    private static void createHorizontalHallway(TETile[][] world, int x1, int x2, int y) {
        int startX = Math.max(0, Math.min(x1, x2));
        int endX = Math.min(world.length - 1, Math.max(x1, x2));

        // 墙壁
        for (int x = startX - 1; x <= endX + 1; x++) {
            if (y + 1 < world[0].length && world[x][y + 1] == Tileset.NOTHING) {
                world[x][y + 1] = Tileset.WALL;
            }
            if (y - 1 >= 0 && world[x][y - 1] == Tileset.NOTHING) {
                world[x][y - 1] = Tileset.WALL;
            }
        }

        for (int x = startX; x <= endX; x++) {
            world[x][y] = Tileset.FLOOR;
        }

    }

    /**
     * 创建垂直走廊
     */
    private static void createVerticalHallway(TETile[][] world, int y1, int y2, int x) {
        int startY = Math.max(0, Math.min(y1, y2));
        int endY = Math.min(world[0].length - 1, Math.max(y1, y2));

        // 墙壁
        for (int y = startY -1 ; y <= endY + 1; y++) {
            if (x + 1 < world.length && world[x + 1][y] == Tileset.NOTHING) {
                world[x + 1][y] = Tileset.WALL;
            }
            if (x - 1 >= 0 && world[x - 1][y] == Tileset.NOTHING) {
                world[x - 1][y] = Tileset.WALL;
            }
        }

        for (int y = startY; y <= endY; y++) {
            world[x][y] = Tileset.FLOOR;
        }


    }


    /**
     * private Position class
     */
    private static class Position {
        int x;
        int y;

        Position (int x, int y) {
            this.x = x;
            this.y = y;
        }
    }




    /**
     * the test method of room
     */
    public static void fillWithNothing(TETile[][] world) {
        int height = world[0].length;
        int width = world.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    public static void main(String[] args) {
        long seed = 919;
        int WIDTH = 80;
        int HEIGHT = 30;
        Random r = new Random(seed);
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillWithNothing(world);

        List<Room> rooms = genManyRoom(world, r, WIDTH, HEIGHT);
        List<int[]> mstEdges = computeMST(rooms);
        generateHallways(world, rooms, mstEdges,r);

        System.out.println(world[16][9].description());

        ter.renderFrame(world);

    }


}
