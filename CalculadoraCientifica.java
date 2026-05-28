import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculadoraCientifica extends JFrame implements ActionListener {
    private JTextField pantalla;
    
    // Variables para el sistema de Memoria y Ans
    private double memoria = 0;
    private double ultimoResultado = 0;

    public CalculadoraCientifica() {
        setTitle("Calculadora Científica Ultra Pro");
        setSize(650, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Configuración de la pantalla
        pantalla = new JTextField();
        pantalla.setFont(new Font("Arial", Font.BOLD, 28));
        pantalla.setHorizontalAlignment(JTextField.RIGHT);
        pantalla.setEditable(false);
        pantalla.setBackground(Color.WHITE);
        add(pantalla, BorderLayout.NORTH);

        // Panel de botones (8 filas, 6 columnas)
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(8, 6, 4, 4));

        // Distribución de todos los botones solicitados
        String[] botones = {
            "MC", "MR", "M+", "M-", "Ans", "C",
            "Sin", "Cos", "Tan", "log", "ln", "CE",
            "Asin", "Acos", "Atan", "Rad", "Deg", "/",
            "x²", "x³", "^", "√", "³√", "*",
            "π", "e", "!", "1/x", "Abs", "-",
            "nCr", "nPr", "(", ")", "%", "+",
            "7", "8", "9", "4", "5", "6",
            "1", "2", "3", "0", ".", "="
        };

        for (String texto : botones) {
            JButton boton = new JButton(texto);
            boton.setFont(new Font("Arial", Font.PLAIN, 15));
            boton.addActionListener(this);
            panelBotones.add(boton);
        }

        add(panelBotones, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        // 1. LÓGICA DEL SISTEMA DE MEMORIA Y ANS
        if (comando.equals("MC")) {
            memoria = 0;
        } else if (comando.equals("MR")) {
            pantalla.setText(pantalla.getText() + memoria);
        } else if (comando.equals("M+")) {
            try {
                if (!pantalla.getText().isEmpty()) memoria += evaluar(pantalla.getText());
            } catch (Exception ex) { pantalla.setText("Error"); }
        } else if (comando.equals("M-")) {
            try {
                if (!pantalla.getText().isEmpty()) memoria -= evaluar(pantalla.getText());
            } catch (Exception ex) { pantalla.setText("Error"); }
        } else if (comando.equals("Ans")) {
            pantalla.setText(pantalla.getText() + "ans");
        } 
        // 2. BOTONES DE LIMPIEZA
        else if (comando.equals("C")) {
            pantalla.setText("");
        } else if (comando.equals("CE")) {
            String texto = pantalla.getText();
            if (!texto.isEmpty()) {
                pantalla.setText(texto.substring(0, texto.length() - 1));
            }
        } 
        // 3. CONVERSIÓN DE ÁNGULOS DIRECTOS (Deg / Rad)
        else if (comando.equals("Deg") || comando.equals("Rad")) {
            try {
                if (!pantalla.getText().isEmpty()) {
                    double valor = evaluar(pantalla.getText());
                    double res = comando.equals("Deg") ? Math.toDegrees(valor) : Math.toRadians(valor);
                    pantalla.setText(String.valueOf(res));
                }
            } catch (Exception ex) { pantalla.setText("Error"); }
        }
        // 4. BOTÓN IGUAL (PROCESAR EXPRESIÓN)
        else if (comando.equals("=")) {
            try {
                double resultado = evaluar(pantalla.getText());
                ultimoResultado = resultado; // Guardamos en Ans para la próxima cuenta
                
                if (resultado == (long) resultado) {
                    pantalla.setText(String.valueOf((long) resultado));
                } else {
                    pantalla.setText(String.valueOf(resultado));
                }
            } catch (Exception ex) {
                pantalla.setText("Error");
            }
        } 
        // 5. MAPEO DE BOTONES COMPLEJOS A TEXTO
        else {
            switch (comando) {
                case "π" -> pantalla.setText(pantalla.getText() + "pi");
                case "e" -> pantalla.setText(pantalla.getText() + "e");
                case "x²" -> pantalla.setText(pantalla.getText() + "^2");
                case "x³" -> pantalla.setText(pantalla.getText() + "^3");
                case "³√" -> pantalla.setText(pantalla.getText() + "cbrt(");
                case "√" -> pantalla.setText(pantalla.getText() + "sqrt(");
                case "1/x" -> pantalla.setText(pantalla.getText() + "1/");
                case "Abs" -> pantalla.setText(pantalla.getText() + "abs(");
                case "Sin", "Cos", "Tan", "log", "ln", "Asin", "Acos", "Atan" -> 
                     pantalla.setText(pantalla.getText() + comando.toLowerCase() + "(");
                case "nCr" -> pantalla.setText(pantalla.getText() + "C");
                case "nPr" -> pantalla.setText(pantalla.getText() + "P");
                default -> pantalla.setText(pantalla.getText() + comando);
            }
        }
    }

    // --- MOTOR EVALUADOR AMPLIADO ---
    public double evaluar(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Error");
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else if (eat('%')) x = x % parseFactor();
                    // Combinatoria nCr y nPr incorporada en la jerarquía de términos
                    else if (eat('C')) x = combinacion((int)x, (int)parseFactor());
                    else if (eat('P')) x = permutacion((int)x, (int)parseFactor());
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); 
                if (eat('-')) return -parseFactor(); 

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') {
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    
                    if      (func.equals("pi"))  x = Math.PI;
                    else if (func.equals("e"))   x = Math.E;
                    else if (func.equals("ans")) x = ultimoResultado;
                    else {
                        x = parseExpression(); // Permite meter expresiones dentro de las funciones, ej: sin(45+45)
                        switch (func) {
                            case "sin"  -> x = Math.sin(Math.toRadians(x));
                            case "cos"  -> x = Math.cos(Math.toRadians(x));
                            case "tan"  -> x = Math.tan(Math.toRadians(x));
                            case "asin" -> x = Math.toDegrees(Math.asin(x));
                            case "acos" -> x = Math.toDegrees(Math.acos(x));
                            case "atan" -> x = Math.toDegrees(Math.atan(x));
                            case "log"  -> x = Math.log10(x);
                            case "ln"   -> x = Math.log(x);
                            case "sqrt" -> x = Math.sqrt(x);
                            case "cbrt" -> x = Math.cbrt(x);
                            case "abs"  -> x = Math.abs(x);
                            default     -> throw new RuntimeException("Error");
                        }
                    }
                } else {
                    throw new RuntimeException("Error");
                }

                if (eat('^')) x = Math.pow(x, parseFactor());
                if (eat('!')) x = factorial((int)x);

                return x;
            }

            // Métodos auxiliares matemáticos
            double factorial(int n) {
                if (n < 0) return 0;
                double fact = 1;
                for (int i = 2; i <= n; i++) fact *= i;
                return fact;
            }

            double permutacion(int n, int r) {
                if (n < r) return 0;
                return factorial(n) / factorial(n - r);
            }

            double combinacion(int n, int r) {
                if (n < r) return 0;
                return factorial(n) / (factorial(r) * factorial(n - r));
            }
        }.parse();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CalculadoraCientifica().setVisible(true);
        });
    }
}