import java.util.Scanner;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class MUDController {
    private Player player;
    private boolean running;

    public MUDController(Player player) {
        this.player = player;
        this.running = false;
    }

    public void runGameLoop() {
        running = true;
        Scanner scanner = new Scanner(System.in);

        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine();
            handleInput(input);
        }

        scanner.close();
    }

    private void handleInput(String input) {
        List<String> tokens = new ArrayList<>(Arrays.asList(input.trim().split("\\s+")));
        if (tokens.isEmpty()) {
            return;
        }

        String command = tokens.get(0).toLowerCase();
        tokens.remove(0);

        switch (command) {
            case "look":
                lookAround();
                break;
            case "move":
                if (tokens.isEmpty()) {
                    System.out.println("Direction required. Usage: move <forward|back|left|right>");
                } else {
                    String direction = tokens.get(0);
                    move(direction);
                }
                break;
            case "pick":
                if (!tokens.isEmpty() && tokens.get(0).equalsIgnoreCase("up")) {
                    tokens.remove(0);
                    if (tokens.isEmpty()) {
                        System.out.println("Item name required. Usage: pick up <itemName>");
                    } else {
                        String itemName = String.join(" ", tokens);
                        pickUp(itemName);
                    }
                } else {
                    System.out.println("Invalid command. Usage: pick up <itemName>");
                }
                break;
            case "inventory":
                checkInventory();
                break;
            case "help":
                showHelp();
                break;
            case "quit":
            case "exit":
                running = false;
                System.out.println("Goodbye!");
                break;
            default:
                System.out.println("Unknown command.");
                break;
        }
    }

    private void lookAround() {
        Room currentRoom = player.getCurrentRoom();
        System.out.println(currentRoom.getName());
        System.out.println(currentRoom.getDescription());

        List<Item> items = currentRoom.getItems();
        if (!items.isEmpty()) {
            System.out.print("Items here: ");
            for (int i = 0; i < items.size(); i++) {
                if (i > 0) {
                    System.out.print(", ");
                }
                System.out.print(items.get(i).getName());
            }
            System.out.println();
        } else {
            System.out.println("No items here.");
        }
    }

    private void move(String direction) {
        Room currentRoom = player.getCurrentRoom();
        Room nextRoom = null;

        switch (direction.toLowerCase()) {
            case "forward":
                nextRoom = currentRoom.getForward();
                break;
            case "back":
                nextRoom = currentRoom.getBack();
                break;
            case "left":
                nextRoom = currentRoom.getLeft();
                break;
            case "right":
                nextRoom = currentRoom.getRight();
                break;
            default:
                System.out.println("You can't go that way!");
                return;
        }

        if (nextRoom != null) {
            player.setCurrentRoom(nextRoom);
            lookAround();
        } else {
            System.out.println("You can't go that way!");
        }
    }

    private void pickUp(String itemName) {
        Room currentRoom = player.getCurrentRoom();
        Item item = currentRoom.getItem(itemName);

        if (item != null) {
            currentRoom.removeItem(item);
            player.addItem(item);
            System.out.println("You picked up " + item.getName());
        } else {
            System.out.println("No item named " + itemName + " here!");
        }
    }

    private void checkInventory() {
        List<Item> inventory = player.getInventory();
        if (inventory.isEmpty()) {
            System.out.println("You are not carrying anything.");
        } else {
            System.out.println("You are carrying:");
            inventory.forEach(item -> System.out.println("- " + item.getName()));
        }
    }

    private void showHelp() {
        System.out.println("Available commands:");
        System.out.println("look - Describes the current room.");
        System.out.println("move <forward|back|left|right> - Moves in the specified direction.");
        System.out.println("pick up <itemName> - Picks up an item from the ground.");
        System.out.println("inventory - Lists items in your inventory.");
        System.out.println("help - Shows this help message.");
        System.out.println("quit/exit - Exits the game.");
    }

    public static void main(String[] args) {
        Room startRoom = new Room("Start Room", "A small stone chamber.");
        Room nextRoom = new Room("Dark Corridor", "A dark, narrow corridor.");
        startRoom.setForward(nextRoom);
        nextRoom.setBack(startRoom);
        Item sword = new Item("sword");
        startRoom.addItem(sword);
        Player player = new Player(startRoom);
        MUDController controller = new MUDController(player);
        controller.runGameLoop();
    }
}

class Player {
    private Room currentRoom;
    private List<Item> inventory = new ArrayList<>();

    public Player(Room startRoom) {
        this.currentRoom = startRoom;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public List<Item> getInventory() {
        return inventory;
    }
}

class Room {
    private String name;
    private String description;
    private Room forward;
    private Room back;
    private Room left;
    private Room right;
    private List<Item> items = new ArrayList<>();

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Room getForward() {
        return forward;
    }

    public void setForward(Room forward) {
        this.forward = forward;
    }

    public Room getBack() {
        return back;
    }

    public void setBack(Room back) {
        this.back = back;
    }

    public Room getLeft() {
        return left;
    }

    public void setLeft(Room left) {
        this.left = left;
    }

    public Room getRight() {
        return right;
    }

    public void setRight(Room right) {
        this.right = right;
    }

    public List<Item> getItems() {
        return items;
    }

    public Item getItem(String itemName) {
        for (Item item : items) {
            if (item.getName().equals(itemName)) {
                return item;
            }
        }
        return null;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }
}

class Item{
    private String name;

    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
