import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String continueChoice = "";

        do {
            System.out.println("--- Austin's IP Address Finder ---");

            String method = "";
            while (!(method.equals("flsm") || method.equals("vlsm"))) {
                System.out.print("Please enter your method of subnetting (flsm/vlsm): ");
                method = sc.nextLine().trim().toLowerCase();

                if (!(method.equals("flsm") || method.equals("vlsm"))) {
                    System.out.println("Invalid method. Please enter either 'flsm' or 'vlsm'.");
                }
            }

            if (method.equals("flsm")) {
                System.out.print("Please enter the initial IP address (e.g., 192.168.1.0): ");
                String ip = sc.nextLine().trim();
                if (!isValidIPAddress(ip)) {
                    System.out.println("Invalid IP address format. Each octet must be between 0 and 255.");
                    continue;
                }
                String[] ipParts = ip.split("\\.");


                int firstOctet, secondOctet, thirdOctet, fourthOctet;
                try {
                    firstOctet = Integer.parseInt(ipParts[0]);
                    secondOctet = Integer.parseInt(ipParts[1]);
                    thirdOctet = Integer.parseInt(ipParts[2]);
                    fourthOctet = Integer.parseInt(ipParts[3]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid numbers in IP address.");
                    return;
                }

                System.out.print("Enter the number of links required: ");
                int numberOfSwitches = sc.nextInt();
                List<Integer> hostRequirements = new ArrayList<>();
                for (int i = 0; i < numberOfSwitches; i++) {
                    System.out.print("Enter number of hosts for switch " + (i + 1) + ": ");
                    hostRequirements.add(sc.nextInt());
                }

                System.out.print("Please enter the fixed number of bits (24 to 30): ");
                int numberOfBits = sc.nextInt();

                int[] blockSizes = {256, 128, 64, 32, 16, 8, 4};
                String[] cidrNotations = {
                        "/24", "/25", "/26", "/27", "/28", "/29", "/30"
                };
                String[] subnetMasks = {
                        "255.255.255.0", "255.255.255.128", "255.255.255.192",
                        "255.255.255.224", "255.255.255.240", "255.255.255.248",
                        "255.255.255.252"
                };

                if (numberOfBits < 24 || numberOfBits > 30) {
                    System.out.println("Invalid number of bits. Must be between 24 and 30.");
                    return;
                }

                int j = numberOfBits - 24;

                int blockSize = blockSizes[j];
                String cidr = cidrNotations[j];
                String subnetMask = subnetMasks[j];

                System.out.println("\n--- IP Allocation Result (FLSM) ---");

                for (int i = 0; i < numberOfSwitches; i++) {
                    int requiredHosts = hostRequirements.get(i);

                    int startFourth = fourthOctet;
                    int startThird = thirdOctet;
                    int endFourth = fourthOctet + blockSize - 1;
                    int endThird = thirdOctet;
                    if (endFourth > 255) {
                        endFourth -= 256;
                        endThird++;
                    }

                    System.out.println("Switch " + (i + 1));
                    System.out.println("Hosts required: " + requiredHosts);
                    System.out.println("Subnet: " + cidr + "  |  Mask: " + subnetMask);
                    System.out.println("IP Range: " +
                            firstOctet + "." + secondOctet + "." + startThird + "." + startFourth +
                            " - " +
                            firstOctet + "." + secondOctet + "." + endThird + "." + endFourth
                    );
                    System.out.println("_________________________");

                    // Advance to next block
                    fourthOctet += blockSize;
                    if (fourthOctet > 255) {
                        fourthOctet -= 256;
                        thirdOctet++;
                    }
                    if (thirdOctet > 255) {
                        System.out.println("IP range exhausted. Cannot allocate more IPs.");
                        break;
                    }

                }

            } else if (method.equals("vlsm")) {

                System.out.print("Please enter the initial IP address (e.g., 192.168.1.0): ");
                String ip = sc.nextLine().trim();
                if (!isValidIPAddress(ip)) {
                    System.out.println("Invalid IP address format. Each octet must be between 0 and 255.");
                    continue;
                }
                String[] ipParts = ip.split("\\.");

                int firstOctet, secondOctet, thirdOctet, fourthOctet;
                try {
                    firstOctet = Integer.parseInt(ipParts[0]);
                    secondOctet = Integer.parseInt(ipParts[1]);
                    thirdOctet = Integer.parseInt(ipParts[2]);
                    fourthOctet = Integer.parseInt(ipParts[3]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid numbers in IP address.");
                    return;
                }

                System.out.println("Enter the number of links required:");
                int numberOfSwitches = sc.nextInt();
                List<Integer> hostRequirements = new ArrayList<>();
                for (int i = 0; i < numberOfSwitches; i++) {
                    System.out.print("Enter number of hosts for switch " + (i + 1) + ": ");
                    hostRequirements.add(sc.nextInt());
                }

                hostRequirements.sort(Collections.reverseOrder());

                int[] blockSizes = {256, 128, 64, 32, 16, 8, 4};
                String[] cidrNotations = {
                        "/24", "/25", "/26", "/27", "/28", "/29", "/30"
                };
                String[] subnetMasks = {
                        "255.255.255.0", "255.255.255.128", "255.255.255.192",
                        "255.255.255.224", "255.255.255.240", "255.255.255.248",
                        "255.255.255.252"
                };
                String[] explanation = {
                        "/24 = 1 subnet, 256 hosts → 254 usable",
                        "/25 = 2 subnets, 128 hosts → 126 usable",
                        "/26 = 4 subnets,  64 hosts →  62 usable",
                        "/27 = 8 subnets,  32 hosts →  30 usable",
                        "/28 =16 subnets,  16 hosts →  14 usable",
                        "/29 =32 subnets,   8 hosts →   6 usable",
                        "/30 =64 subnets,   4 hosts →   2 usable"
                };

                System.out.println("\n--- IP Allocation Result (VLSM) ---");
                for (int i = 0; i < numberOfSwitches; i++) {
                    int requiredHosts = hostRequirements.get(i);

                    // explicit if-else ladder:
                    int j;
                    if (requiredHosts <= 2) j = 6;  // /30
                    else if (requiredHosts <= 6) j = 5;  // /29
                    else if (requiredHosts <= 14) j = 4;  // /28
                    else if (requiredHosts <= 30) j = 3;  // /27
                    else if (requiredHosts <= 62) j = 2;  // /26
                    else if (requiredHosts <= 126) j = 1;  // /25
                    else j = 0;  // /24

                    int blockSize = blockSizes[j];
                    String cidr = cidrNotations[j];
                    String subnetMask = subnetMasks[j];
                    String explanationText = explanation[j];

                    // calculate range
                    int startFourth = fourthOctet;
                    int startThird = thirdOctet;
                    int endFourth = fourthOctet + blockSize - 1;
                    int endThird = thirdOctet;
                    if (endFourth > 255) {
                        endFourth -= 256;
                        endThird++;
                    }

                    // print results
                    System.out.println("Switch " + (i + 1));
                    System.out.println("Hosts required: " + requiredHosts);
                    System.out.println("Subnet: " + cidr + "  |  Mask: " + subnetMask);
                    System.out.println("IP Range: " +
                            firstOctet + "." + secondOctet + "." + startThird + "." + startFourth +
                            " - " +
                            firstOctet + "." + secondOctet + "." + endThird + "." + endFourth
                    );
                    System.out.println("_________________________");

                    // advance to next block
                    fourthOctet += blockSize;
                    if (fourthOctet > 255) {
                        fourthOctet -= 256;
                        thirdOctet++;
                    }
                    if (thirdOctet > 255) {
                        System.out.println("IP range exhausted. Cannot allocate more IPs.");
                        break;
                    }

                }
            }

            sc.nextLine();
            System.out.print("\nDo you wish to continue? (Press Enter to continue, type 'e' to exit): ");
            continueChoice = sc.nextLine().trim().toLowerCase();

        } while (!(continueChoice.equals("e") || continueChoice.equals("end")));

        System.out.println("Program ended. Goodbye!");
        sc.close();
    }

    public static boolean isValidIPAddress(String ip) {
        return ip.matches("^((25[0-5]|2[0-4][0-9]|[01]?\\d\\d?)(\\.|$)){4}");
    }

}
