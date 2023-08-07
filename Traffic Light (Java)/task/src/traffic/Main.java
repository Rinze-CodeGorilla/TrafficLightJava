package traffic;

import java.util.Scanner;

public class Main {
    final static Scanner scanner = new Scanner(System.in);

    volatile private static boolean isQuitting = false;
    volatile private static boolean isOpened = false;

    volatile private static CircularQueue roads;
    volatile private static int interval;

    public static void main(String[] args) {
        System.out.println("Welcome to the traffic management system!");
        System.out.println("Input the number of roads: ");
        roads = new CircularQueue(scanInt());
        System.out.println("Input the interval:");
        interval = scanInt();
        var queueThread = new Thread(Main::getQueueThreadHandler, "QueueThread");
        queueThread.start();

        while (!isQuitting) {
            System.out.print("""
                    Menu:
                    1. Add
                    2. Delete
                    3. System
                    0. Quit
                    """);
            switch (scanner.nextLine()) {
                case "1" -> {
                    System.out.println("Input your road name");
                    var road = scanner.nextLine();
                    try {
                        roads.enqueue(road);
                        System.out.println(road + " added!");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                case "2" -> {
                    try {
                        var road = roads.dequeue();
                        System.out.println(road + " deleted!");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                case "3" -> {
                    isOpened = true;
                }
                case "0" -> {
                    isQuitting = true;
                    queueThread.interrupt();
                    System.out.println("Bye!");
                }
                default -> System.out.println("Incorrect option");
            }
            if (!isQuitting) {
                scanner.nextLine();
            }
            if (isOpened) {
                isOpened = false;
            }
        }
    }

    private static void getQueueThreadHandler() {
        var deltaTime = 0;
        while (!isQuitting) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            if (isOpened) {
                displayState(deltaTime);
            }
            deltaTime++;
            if (deltaTime % interval == 0) {
                roads.openNext();
            }
        }
    }

    private static void displayState(int deltaTime) {
        System.out.println("""
                        ! %ds. have passed since system startup !
                        ! Number of roads: %d !
                        ! Interval: %d !
                        """.formatted(deltaTime, roads.maxSize(), interval));

        for (var roadState :
                roads.getState()) {
            var name = roadState.name();
            var state = roadState.waitInterval() == 0 ? "open" : "closed";
            var time = roadState.waitInterval() * interval;
            if (roadState.waitInterval() == 0) {
                time = interval;
            }
            time -= deltaTime % interval;
            System.out.println("""
                            Road "%s" will be %s for %ds.""".formatted(name, state, time));
        }
        System.out.println("""
                        
                        ! Press "Enter" to open menu !""");
    }
    public static int scanInt() {
        while (true) {
            try {
                var result = Integer.parseInt(scanner.nextLine());
                if (result > 0) {
                    return result;
                }
            } catch (Exception ignored) {
            }
            System.out.println("Error! Incorrect Input. Try again: ");
        }
    }
}
