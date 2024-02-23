package Interpreter;

public class Integer32Value implements RuntimeValue {
    public int value;

    public Integer32Value(int value) {
        this.value = value;
    }

    @Override
    public void log() {
        System.out.printf("Integer Value: %d\n", value);
    }
}
