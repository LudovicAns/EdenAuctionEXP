package fr.edencraft.edenauctionexp.content;

public class ConfigContent {

    public static String CONTENT;

    static {
        CONTENT = """
                #====================================================================================#
                #     ______    __           ___              __  _             _______  __ ____     #
                #    / ____/___/ /__  ____  /   | __  _______/ /_(_)___  ____  / ____/ |/ // __ \\    #
                #   / __/ / __  / _ \\/ __ \\/ /| |/ / / / ___/ __/ / __ \\/ __ \\/ __/  |   // /_/ /    #
                #  / /___/ /_/ /  __/ / / / ___ / /_/ / /__/ /_/ / /_/ / / / / /___ /   |/ ____/     #
                # /_____/\\__,_/\\___/_/ /_/_/  |_\\__,_/\\___/\\__/_/\\____/_/ /_/_____//_/|_/_/          #
                #                                                                                    #
                #====================================================================================#
                                
                # DO NOT TOUCH THIS IF YOU DON'T KNOW WHAT YOU ARE DOING !
                config-version: 1.
                
                # Minimum price for 1 EXP. (1 EXP ≠ 1 Level)
                min-price: 0.001
                
                # Maximum price for 1 EXP. (1 EXP ≠ 1 Level)
                max-price: 1
                
                # Minimum amount of exp needed to place an order.
                min-experience: 1000
                
                # Maximum amount of exp in one order.
                max-experience: 100000
                
                # Maximum order number that a player can place.
                max-order: 100
                
                #=======================================================================#
                #     ___              __  _                __  ___                     #
                #    /   | __  _______/ /_(_)___  ____     /  |/  /__  ____  __  __     #
                #   / /| |/ / / / ___/ __/ / __ \\/ __ \\   / /|_/ / _ \\/ __ \\/ / / /     #
                #  / ___ / /_/ / /__/ /_/ / /_/ / / / /  / /  / /  __/ / / / /_/ /      #
                # /_/  |_\\__,_/\\___/\\__/_/\\____/_/ /_/  /_/  /_/\\___/_/ /_/\\__,_/       #
                #                                                                       #
                #=======================================================================#
                
                # In this section you can customize/translate all items from the Auction menu.
                items:
                  # Item for buying orders.
                  # You can use different variables in displayname and lore section:
                  # 👉 order_id :       ID of the order.
                  # 👉 player_name :    Name of the player that send the order.
                  # 👉 exp :            Amount of exp requested by the order.
                  # 👉 price :          Amount of money paid for the order.
                  buy-order:
                      displayname: '&cOrdre d''achat &3#{order_id}'
                      lore:
                      - ''
                      - '&7Ordre placé par &b{player_name}'
                      - ''
                      - '&7Expérience demandé: &b{exp}'
                      - '&7Prix d''achat: &b{price}'
                      - ''
                      - '&7Nuémro d''ordre en auction: &8{order_id}'
                    # Item for selling orders.
                    # You can use different variables in displayname and lore section:
                    # 👉 order_id :       ID of the order.
                    # 👉 player_name :    Name of the player that send the order.
                    # 👉 exp :            Amount of exp requested by the order.
                    # 👉 price :          Amount of money paid for the order.
                    sell-order:
                      displayname: '&aOrdre de vente &3#{order_id}'
                      lore:
                      - ''
                      - '&7Ordre placé par &b{player_name}'
                      - ''
                      - '&7Expérience en vente: &b{exp}'
                      - '&7Prix de vente: &b{price}'
                      - ''
                      - '&7Nuémro d''ordre en auction: &8{order_id}'
                    # Item for next page.
                    # 👉 next_page_number :   Next page number, no more.
                    next-page:
                      displayname: '&fPage suivante &8(&7{next_page_number}&8)'
                      lore: none
                    # Item for previous page.
                    # 👉 previous_page_number :   Previous page number, no more.
                    previous-page:
                      displayname: '&fPage précédente &8(&7{previous_page_number}&8)'
                      lore: none
                """;
    }
}
