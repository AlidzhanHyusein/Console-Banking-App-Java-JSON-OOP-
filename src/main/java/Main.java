import service.BankService;
import ui.BankUI;

public class Main {
    public static void main(String[] args) {
        BankService bank = new BankService();
        bank.loadData();
        BankUI bankUI = new BankUI();
        bankUI.start();
    }
}
