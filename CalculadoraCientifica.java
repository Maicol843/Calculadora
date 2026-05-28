import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculadoraCientifica extends JFrame implements ActionListener {
    private JTextField pantalla;
    private double memoria = 0;
    private double ultimoResultado = 0;

    public CalculadoraCientifica() {
        setTitle("Calculadora Científica");
        setSize(480, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(28, 28, 28)); // Fondo oscuro general
        setLayout(new BorderLayout(10, 10));

        // 1. PANTALLA ESTILIZADA
        pantalla = new JTextField();
        pantalla.setFont(new Font("Consolas", Font.BOLD, 36));
        pantalla.setHorizontalAlignment(JTextField.RIGHT);
        pantalla.setEditable(false);
        pantalla.setBackground(new Color(40, 40, 40));
        pantalla.setForeground(new Color(230, 230, 230));
        pantalla.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        add(pantalla, BorderLayout.NORTH);

        // 2. PANEL DE BOTONES (Organizado en 8 filas y 6 columnas)
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(8, 6, 6, 6));
        panelBotones.setBackground(new Color(28, 28, 28));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Distribución real de calculadora: Funciones arriba, memoria al medio, números abajo
        String[] botones = {
            // Fila 1: Memoria y Ans
            "MC", "MR", "M+", "M-", "Ans", "CE",
            // Fila 2: Trigonométricas
            "Sin", "Cos", "Tan", "Asin", "Acos", "Atan",
            // Fila 3: Logaritmos, raíces y potencias complejas
            "log", "ln", "√", "³√", "^", "!",
            // Fila 4: Constantes y otras funciones
            "π", "e", "x²", "x³", "1/x", "Abs",
            // Fila 5: Herramientas, paréntesis y división
            "Rad", "Deg", "nCr", "nPr", "(", ")",
            // Fila 6: Bloque numérico tradicional (7,8,9) + Borrar y división
            "7", "8", "9", "%", "C", "/",
            // Fila 7: Bloque numérico (4,5,6) + multiplicación y resta
            "4", "5", "6", " ", " * ", "-",
            // Fila 8: Bloque numérico (1,2,3,0) + suma e igual
            "1", "2", "3", "0", ".", "="
        };

        // Colores de la paleta "Calculadora Moderna"
        Color colorNumeros = new Color(51, 51, 51);       // Gris oscuro
        Color colorFunciones = new Color(74, 74, 74);     // Gris medio
        Color colorMemoria = new Color(60, 63, 65);       // Gris azulado
        Color colorBorrar = new Color(217, 83, 79);       // Rojo / Salmón
        Color colorIgual = new Color(240, 173, 78);       // Naranja / Dorado
        Color colorTexto = Color.WHITE;

        for (String texto : botones) {
            // El espacio en blanco es para rellenar la grilla estéticamente
            if (texto.equals(" ")) {
                panelBotones.add(new JLabel(""));
                continue;
            }

            JButton boton = new JButton(texto.trim());
            boton.setFont(new Font("Arial", Font.BOLD, 14));
            boton.setForeground(colorTexto);
            boton.setFocusPainted(false);
            boton.setBorder(BorderFactory.createLineBorder(new Color(45, 45, 45), 1));

            // Asignación de colores según el tipo de botón
            if (texto.equals("=")) {
                boton.setBackground(colorIgual);
                boton.setForeground(new Color(40, 40, 40)); // Texto oscuro para contraste
                boton.setFont(new Font("Arial", Font.BOLD, 18));
            } else if (texto.equals("C") || texto.equals("CE")) {
                boton.setBackground(colorBorrar);
            } else if (texto.matches("[0-9.]")) {
                boton.setBackground(colorNumeros);
                boton.setFont(new Font("Arial", Font.BOLD, 18)); // Números más grandes
            } else if (texto.matches("MC|MR|M\\+|M-|Ans")) {
                boton.setBackground(colorMemoria);
                boton.setFont(new Font("Arial", Font.ITALIC, 13));
            } else {
                boton.setBackground(colorFunciones); // Operaciones y funciones científicas
            }

            // Efecto sutil al pasar el mouse por encima (Hover)
            boton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    boton.setBackground(boton.getBackground().brighter());
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    boton.setBackground(boton.getBackground().darker());
                }
            });

            boton.addActionListener(this);
            panelBotones.add(boton);
        }

        add(panelBotones, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        if (comando.equals("MC")) {
            memoria = 0;
        } else if (comando.equals("MR")) {
            pantalla.setText(pantalla.getText() + memoria);
        } else if (comando.equals("M+")) {
            try { if (!pantalla.getText().isEmpty()) memoria += evaluar(pantalla.getText()); } catch (Exception ex) { pantalla.setText("Error"); }
        } else if (comando.equals("M-")) {
            try { if (!pantalla.getText().isEmpty()) memoria -= evaluar(pantalla.getText()); } catch (Exception ex) { pantalla.setText("Error"); }
        } else if (comando.equals("Ans")) {
            pantalla.setText(pantalla.getText() + "ans");
        } else if (comando.equals("C")) {
            pantalla.setText("");
        } else if (comando.equals("CE")) {
            String texto = pantalla.getText();
            if (!texto.isEmpty()) pantalla.setText(texto.substring(0, texto.length() - 1));
        } else if (comando.equals("Deg") || comando.equals("Rad")) {
            try {
                if (!pantalla.getText().isEmpty()) {
                    double valor = evaluar(pantalla.getText());
                    double res = comando.equals("Deg") ? Math.toDegrees(valor) : Math.toRadians(valor);
                    pantalla.setText(String.valueOf(res));
                }
            } catch (Exception ex) { pantalla.setText("Error"); }
        } else if (comando.equals("=")) {
            try {
                double resultado = evaluar(pantalla.getText());
                ultimoResultado = resultado;
                if (resultado == (long) resultado) {
                    pantalla.setText(String.valueOf((long) resultado));
                } else {
                    pantalla.setText(String.valueOf(resultado));
                }
            } catch (Exception ex) {
                pantalla.setText("Error");
            }
        } else {
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

    // --- MOTOR EVALUADOR ---
    public double evaluar(final String str) {
        return new Object() {
            int pos = -1, ch;
            void nextChar() { ch = (++pos < str.length()) ? str.charAt(pos) : -1; }
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) { nextChar(); return true; }
                return false;
            }
            double parse() { nextChar(); double x = parseExpression(); if (pos < str.length()) throw new RuntimeException("Error"); return x; }
            double parseExpression() {
                double x = parseTerm();
                for (;;) { if (eat('+')) x += parseTerm(); else if (eat('-')) x -= parseTerm(); else return x; }
            }
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else if (eat('%')) x = x % parseFactor();
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
                if (eat('(')) { x = parseExpression(); eat(')'); }
                else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') {
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if      (func.equals("pi"))  x = Math.PI;
                    else if (func.equals("e"))   x = Math.E;
                    else if (func.equals("ans")) x = ultimoResultado;
                    else {
                        x = parseExpression();
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
                } else { throw new RuntimeException("Error"); }
                if (eat('^')) x = Math.pow(x, parseFactor());
                if (eat('!')) x = factorial((int)x);
                return x;
            }
            double factorial(int n) { if (n < 0) return 0; double fact = 1; for (int i = 2; i <= n; i++) fact *= i; return fact; }
            double permutacion(int n, int r) { if (n < r) return 0; return factorial(n) / factorial(n - r); }
            double combinacion(int n, int r) { if (n < r) return 0; return factorial(n) / (factorial(r) * factorial(n - r)); }
        }.parse();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CalculadoraCientifica().setVisible(true);
        });
    }
}