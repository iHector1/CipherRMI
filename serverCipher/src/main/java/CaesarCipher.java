import java.util.concurrent.RecursiveTask;

public class CaesarCipher extends RecursiveTask<String> {
    private static final int THRESHOLD = 100000;
    private final String message;
    private final int offset;

    public CaesarCipher(String message, int offset) {
        this.message = message.toLowerCase();
        this.offset = (offset < 0 ? 26 - offset : offset);
    }
    public static String cipher(String message, int offset) {
        StringBuilder result = new StringBuilder();
        message = message.toLowerCase();

        for (char character : message.toCharArray()) {
            if (character >= 'a' && character <= 'z') {
                int originalAlphabetPosition = character - 'a';
                int newAlphabetPosition = (originalAlphabetPosition + offset) % 26;
                char newCharacter = (char) ('a' + newAlphabetPosition);

                result.append(newCharacter);
            } else {
                result.append(character);
            }
        }

        return result.toString();
    }

    @Override
    protected String compute() {
        if (message.length() > THRESHOLD) {
            int mid = message.length() / 2;
            CaesarCipher firstSubtask = new CaesarCipher(message.substring(0, mid), offset);
            CaesarCipher secondSubtask = new CaesarCipher(message.substring(mid), offset);

            secondSubtask.fork();

            return firstSubtask.compute() + secondSubtask.join();
        } else{
            return cipher(message, offset);
        }
    }
}