import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Produto implements Serializable {
    private String nome;
    private double preco;
    private double desconto; 

    public Produto(String nome, double preco, double desconto) {
        this.nome = nome;
        this.preco = preco;
        this.desconto = desconto;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public double getDesconto() {
        return desconto;
    }

    public double getPrecoComDesconto() {
        return preco * (1 - desconto / 100); // Calculo do preco na % 
    }

    @Override
    public String toString() {
        return nome + " - R$" + getPrecoComDesconto() + " (" + desconto + "% de desconto)";
    }
}

public class CadastroProdutosApp extends JFrame {
    private List<Produto> produtos;
    private JList<Produto> produtosList;
    private DefaultListModel<Produto> listModel;

    public CadastroProdutosApp() {
        super("Cadastro de Produtos");

        produtos = new ArrayList<>();
        listModel = new DefaultListModel<>();
        produtosList = new JList<>(listModel);

        JButton cadastrarButton = new JButton("Cadastrar");
        JButton editarButton = new JButton("Editar");
        JButton excluirButton = new JButton("Excluir");

        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarProduto();
            }
        });

        editarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarProduto();
            }
        });

        excluirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excluirProduto();
            }
        });

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3));
        buttonsPanel.add(cadastrarButton);
        buttonsPanel.add(editarButton);
        buttonsPanel.add(excluirButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(produtosList), BorderLayout.CENTER);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        carregarProdutos(); // Criei essa funcao para carregar produtos salvos ao iniciar o aplicativo
    }

    private void cadastrarProduto() {
        String nome = JOptionPane.showInputDialog(this, "Nome do produto:");
        if (nome != null && !nome.trim().isEmpty()) {
            String precoStr = JOptionPane.showInputDialog(this, "Preço do produto:");
            if (precoStr != null && !precoStr.trim().isEmpty()) {
                try {
                    double preco = Double.parseDouble(precoStr);
                    double desconto = 0;
                    int opcaoDesconto = JOptionPane.showConfirmDialog(this, "Deseja aplicar um desconto?");
                    if (opcaoDesconto == JOptionPane.YES_OPTION) {
                        String descontoStr = JOptionPane.showInputDialog(this, "Desconto (%):");
                        if (descontoStr != null && !descontoStr.trim().isEmpty()) {
                            desconto = Double.parseDouble(descontoStr);
                        }
                    }
                    Produto produto = new Produto(nome, preco, desconto);
                    produtos.add(produto);
                    listModel.addElement(produto);
                    salvarProdutos(); // Salvar produtos após cadastrar um novo
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Por favor, insira um valor válido.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void editarProduto() {
        int selectedIndex = produtosList.getSelectedIndex();
        if (selectedIndex != -1) {
            Produto produto = produtos.get(selectedIndex);
            String novoNome = JOptionPane.showInputDialog(this, "Novo nome do produto:", produto.getNome());
            if (novoNome != null && !novoNome.trim().isEmpty()) {
                String novoPrecoStr = JOptionPane.showInputDialog(this, "Novo preço do produto:", produto.getPreco());
                if (novoPrecoStr != null && !novoPrecoStr.trim().isEmpty()) {
                    try {
                        double novoPreco = Double.parseDouble(novoPrecoStr);
                        double novoDesconto = 0;
                        int opcaoDesconto = JOptionPane.showConfirmDialog(this, "Deseja aplicar um desconto?");
                        if (opcaoDesconto == JOptionPane.YES_OPTION) {
                            String novoDescontoStr = JOptionPane.showInputDialog(this, "Novo desconto (%):",
                                    produto.getDesconto());
                            if (novoDescontoStr != null && !novoDescontoStr.trim().isEmpty()) {
                                novoDesconto = Double.parseDouble(novoDescontoStr);
                            }
                        }
                        produto = new Produto(novoNome, novoPreco, novoDesconto);
                        produtos.set(selectedIndex, produto);
                        listModel.setElementAt(produto, selectedIndex);
                        salvarProdutos(); // Salvar produtos após editar
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Por favor, insira um valor válido.", "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto para editar.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluirProduto() {
        int selectedIndex = produtosList.getSelectedIndex();
        if (selectedIndex != -1) {
            produtos.remove(selectedIndex);
            listModel.remove(selectedIndex);
            salvarProdutos(); // Salvar produtos após excluir
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void salvarProdutos() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("produtos.ser"))) {
            outputStream.writeObject(produtos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarProdutos() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("produtos.ser"))) {
            produtos = (List<Produto>) inputStream.readObject();
            for (Produto produto : produtos) {
                listModel.addElement(produto);
            }
        } catch (IOException | ClassNotFoundException e) {
            // Arquivo de produtos ainda não existe ou ocorreu um erro ao carregar
            // Isso é normal na primeira execução ou quando não há produtos salvos
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CadastroProdutosApp();
            }
        });
    }
}
