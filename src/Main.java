import com.chefswar.controllers.*;
import com.chefswar.models.*;
import com.chefswar.service.FoodDeliveryService;
import com.chefswar.utils.*;

import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final FoodDeliveryService service = FoodDeliveryService.getInstance();

    // Controllers
    private static final CustomerController customerController =
            new CustomerController(scanner, service);
    private static final RestaurantOwnerController restaurantController =
            new RestaurantOwnerController(scanner, service);
    private static final SystemAdminController adminController =
            new SystemAdminController(scanner, service);

    private static final InputValidator inputValidator = new InputValidator(scanner);
    private static User currentUser;

    public static void main(String[] args) {
        DataInitializer.initializeData(service);

        while (true) {
            try {
                if (currentUser == null) {
                    showLoginMenu();
                } else {
                    handleUserMenu();
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Please try again.");
            }
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n=== Food Delivery System ===");
        System.out.println("1. Login");
        System.out.println("2. Exit");

        int choice = inputValidator.getIntInput("Choose option: ", 1, 2);

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                System.out.println("Thank you for using Food Delivery System!");
                System.exit(0);
                break;
        }
    }

    private static void login() {
        String userId = inputValidator.getStringInput("Enter User ID: ");

        Optional<User> user = service.getUser(userId);
        if (user.isPresent()) {
            currentUser = user.get();
            System.out.println("Login successful! Welcome " + currentUser.getName());
        } else {
            System.out.println("User not found! Available users: C001, C002, O001, O002, A001");
        }
    }

    private static void handleUserMenu() {
        boolean continueSession = true;

        switch (currentUser.getRole()) {
            case CUSTOMER:
                continueSession = customerController.handleCustomerMenu((Customer) currentUser);
                break;
            case RESTAURANT_OWNER:
                continueSession = restaurantController.handleRestaurantOwnerMenu((RestaurantOwner) currentUser);
                break;
            case SYSTEM_ADMIN:
                continueSession = adminController.handleSystemAdminMenu((SystemAdmin) currentUser);
                break;
        }

        if (!continueSession) {
            currentUser = null; // User logged out
        }
    }
}
