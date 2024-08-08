import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class StockManagementSystemGUI extends JFrame {
    private DefaultListModel<StockItem> listModel;
    private JList<StockItem> itemListUI;
    private JTextField itemIdField;
    private JTextField itemNameField;
    private JTextField itemQuantityField;
    private JTextField itemPriceField;
    private JButton addButton;
    private JButton buyButton;
    private StockManagementSystem stockSystem;

    public StockManagementSystemGUI() {
        setTitle("Stock Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        listModel = new DefaultListModel<>();
        itemListUI = new JList<>(listModel);
        itemListUI.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only one item selection

        itemIdField = new JTextField(5);
        itemNameField = new JTextField(20);
        itemQuantityField = new JTextField(5);
        itemPriceField = new JTextField(5);

        addButton = new JButton("Add Item");
        buyButton = new JButton("Buy Item");

        stockSystem = new StockManagementSystem();

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Item ID:"));
        inputPanel.add(itemIdField);
        inputPanel.add(new JLabel("Item Name:"));
        inputPanel.add(itemNameField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(itemQuantityField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(itemPriceField);
        inputPanel.add(addButton);
        inputPanel.add(buyButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItem();
            }
        });

        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buyItem();
            }
        });

        setLayout(new BorderLayout());
        add(new JScrollPane(itemListUI), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        updateItemList();
    }

    private void addItem() {
        int id = Integer.parseInt(itemIdField.getText());
        String name = itemNameField.getText();
        int quantity = Integer.parseInt(itemQuantityField.getText());
        double price = Double.parseDouble(itemPriceField.getText());

        StockItem newItem = new StockItem(id, name, quantity, price);
        stockSystem.addStockItem(newItem);
        updateItemList();

        itemIdField.setText("");
        itemNameField.setText("");
        itemQuantityField.setText("");
        itemPriceField.setText("");
    }

    private void buyItem() {
        StockItem selectedItem = itemListUI.getSelectedValue();
        if (selectedItem != null) {
            int itemId = selectedItem.getId();
            int quantityToBuy = Integer.parseInt(JOptionPane.showInputDialog("Enter quantity to buy:"));

            stockSystem.buyStockItem(itemId, quantityToBuy);
            updateItemList();

            // Show an invoice dialog
            showInvoice(selectedItem, quantityToBuy);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to buy.");
        }
    }

    private void showInvoice(StockItem item, int quantity) {
        double totalCost = item.getPrice() * quantity;
        String invoiceMessage = "Invoice\n" +
                "-------\n" +
                "Item: " + item.getName() + "\n" +
                "Quantity: " + quantity + "\n" +
                "Price per unit: $" + item.getPrice() + "\n" +
                "Total Cost: $" + totalCost + "\n" +
                "Remaining Quantity: " + item.getRemainingQuantity() + "\n" +
                "-------------------------";
        JOptionPane.showMessageDialog(this, invoiceMessage, "Invoice", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateItemList() {
        listModel.clear();
        List<StockItem> allItems = stockSystem.getAllStockItems();
        for (StockItem item : allItems) {
            listModel.addElement(item);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                StockManagementSystemGUI app = new StockManagementSystemGUI();
                app.setVisible(true);
            }
        });
    }
}

class StockItem {
    private int id;
    private String name;
    private int quantity;
    private int soldQuantity;
    private double price;

    public StockItem(int id, String name, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.soldQuantity = 0;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public double getPrice() {
        return price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public int getRemainingQuantity() {
        return quantity - soldQuantity;
    }

    @Override
    public String toString() {
        return id + ": " + name + " (Quantity: " + getRemainingQuantity() + ", Price: $" + price + ")";
    }
}

class StockManagementSystem {
    private List<StockItem> stockItems;

    public StockManagementSystem() {
        stockItems = new ArrayList<>();
    }

    public void addStockItem(StockItem item) {
        stockItems.add(item);
    }

    public List<StockItem> getAllStockItems() {
        return stockItems;
    }

    public void buyStockItem(int itemId, int quantity) {
        for (StockItem item : stockItems) {
            if (item.getId() == itemId) {
                int availableQuantity = item.getRemainingQuantity();
                if (quantity <= availableQuantity) {
                    item.setSoldQuantity(item.getSoldQuantity() + quantity);
                    break;
                }
            }
        }
    }
}
