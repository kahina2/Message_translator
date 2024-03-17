import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class Translate {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/magazin";
        String username = "root";
        String password = "";

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select the CSV file to import");
        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
                connection.setAutoCommit(false);
                String sql = "INSERT INTO products(id, marque, nom_produit, type_article, poids, taille, contenance, format, couleur, description, prix_professionnel_ht, prix_public_ht, nom_fournisseur, adresse_fournisseur, telephone_fournisseur) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql);
                     BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                	
                    String lineText;
                    int count = 0;
                    reader.readLine(); // Ignore header line
                    while ((lineText = reader.readLine()) != null) {
                        String[] data = lineText.split(";");
                        String id = data[0];
                        String marque = data[1];
                        String nom_produit = data[2];
                        String type_article = data[3];
                        String poids = data[4];
                        String taille = data[5];
                        String contenance = data[6];
                        String format = data[7];
                        String couleur = data[8];
                        String description = data[9];
                        String prix_professionnel_ht = data[10];
                        String prix_public_ht = data[11];
                        String nom_fournisseur = data[12];
                        String adresse_fournisseur = data[13];
                        String telephone_fournisseur = data[14];

                        statement.setInt(1, Integer.parseInt(id));
                        statement.setString(2, marque);
                        statement.setString(3, nom_produit);
                        statement.setString(4, type_article);
                        statement.setInt(5, Integer.parseInt(poids));
                        statement.setInt(6, Integer.parseInt(taille));
                        statement.setString(7, contenance);
                        statement.setString(8, format);
                        statement.setString(9, couleur);
                        statement.setString(10, description);
                        statement.setBigDecimal(11, new BigDecimal(prix_professionnel_ht));
                        statement.setBigDecimal(12, new BigDecimal(prix_public_ht));
                        statement.setString(13, nom_fournisseur);
                        statement.setString(14, adresse_fournisseur);
                        statement.setString(15, telephone_fournisseur);
                        statement.addBatch();

                        if (++count % 20 == 0) {
                            statement.executeBatch();
                        }
                    }

                    statement.executeBatch();
                    connection.commit();
                    System.out.println("Data has been inserted successfully.");
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
