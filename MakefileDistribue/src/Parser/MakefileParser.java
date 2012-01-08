package Parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MakefileParser {

	public final static byte HT = 9;
	public final static byte LF = 10;
	public final static byte SP = 32; // space
	public final static byte COLON = 58; // colon = ":"
	public final static byte DIESE = 35; // # = commentaire

	@SuppressWarnings("null")
	public static List<Target> parse(String makefileName) {
		FileInputStream fis;
		FileChannel fc;
		ArrayList<Target> listTarget = new ArrayList<Target>();

		try {

			fis = new FileInputStream(new File(makefileName));

			// On récupère le canal
			fc = fis.getChannel();

			// On en déduit la taille
			int size = (int) fc.size();

			// On crée un buffer
			// correspondant à la taille du fichier
			ByteBuffer bBuff = ByteBuffer.allocate(size);

			// Démarrage de la lecture
			fc.read(bBuff);
			// On prépare à la lecture avec l'appel à flip
			bBuff.flip();

			// Vu que nous avons pris un buffer de byte
			// Afin de récupérer les données, nous pouvons utiliser
			// un tableau de byte
			// La méthode array retourne un tableau de byte

			byte[] tabByte;

			if (bBuff.hasArray()) {
				tabByte = bBuff.array();

				// changer la facon de parser, penser acceptation plutot que
				// rejet de caracteres

				Target currentTarget = null;
				for (int i = 0; i < tabByte.length; i++) {
					// on traite le makefile ligne par ligne, on recupere le
					// premier byte de chaque ligne et on analyse
					byte firstByte = tabByte[i];

					StringBuffer strBuff = null;

					switch (firstByte) {
					case HT:
						// tabulation donc c'est une commande, ne pas oublier
						// d'incrementer i quand on va parser la commande (on
						// parse jusqu'au prochain LF), ne pas oublier que cette
						// commande est forcement liee a une cible, une fois la
						// commande parsee, on passe a null la referance sur la
						// target pour indiquer qu'on passe a une autre cible
						strBuff = new StringBuffer();
						// on saute la tabulation directement
						i++;
						while (tabByte[i] != LF) {
							strBuff.append((char) tabByte[i]);
							i++;
						}
						currentTarget.setCmd(strBuff.toString());
						if (currentTarget != null
								&& currentTarget.getName() != null) {
							if (!listTarget.contains(currentTarget)) {
								listTarget.add(currentTarget);
								currentTarget = null;
							}

						} else {
							System.out
									.println("La target actuelle n'avait pas ete creee !\n");
						}
						break;
					case LF:

						// retour chariot donc on passe directement au byte
						// suivant,
						break;
					case DIESE:
						i++;
						while (tabByte[i] != LF) {
							i++;
						}
						break;
					default:
						// par defaut, c'est une cible, on parse jusqu'au
						// prochain ":", puis on parse les dependances, en
						// ignorant les SP et les HT eventuels jusqu'au prochain
						// LF
						if (currentTarget != null) {
							if (!listTarget.contains(currentTarget)) {
								listTarget.add(currentTarget);
							}
						}
						currentTarget = new Target();
						strBuff = new StringBuffer();
						while (tabByte[i] != COLON) {
							strBuff.append((char) tabByte[i]);
							i++;
						}
						currentTarget.setName(strBuff.toString());
						// on saute les 2 points
						i++;
						boolean fin = false;
						while (tabByte[i] != LF) {
							strBuff = new StringBuffer();
							while (tabByte[i] != SP && tabByte[i] != HT) {
								if (tabByte[i] == LF) {
									fin = true;
									break;
								}

								strBuff.append((char) tabByte[i]);
								i++;
							}
							if (strBuff.length() > 0) {
								currentTarget.getDependencies().add(
										strBuff.toString());
							}
							if (fin) {
								break;
							}
							i++;
						}
						break;
					}
				}

			} else {
				System.out.println("bBuff n'a pas d'array\n");
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listTarget;
	}

}
