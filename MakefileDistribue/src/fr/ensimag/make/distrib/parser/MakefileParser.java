package fr.ensimag.make.distrib.parser;

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
	public static List<Rule> parse(String makefileName) {
		FileInputStream fis;
		FileChannel fc;
		ArrayList<Rule> listRule = new ArrayList<Rule>();

		try {

			fis = new FileInputStream(new File(makefileName));


			fc = fis.getChannel();

			int size = (int) fc.size();


			ByteBuffer bBuff = ByteBuffer.allocate(size);


			fc.read(bBuff);

			bBuff.flip();

			byte[] tabByte;

			if (bBuff.hasArray()) {
				tabByte = bBuff.array();



				Rule currentRule = null;
				for (int i = 0; i < tabByte.length; i++) {

					byte firstByte = tabByte[i];

					StringBuffer strBuff = null;

					switch (firstByte) {
					case HT:

						strBuff = new StringBuffer();

						i++;
						while (tabByte[i] != LF) {
							strBuff.append((char) tabByte[i]);
							i++;
						}
						currentRule.setCmd(strBuff.toString());
						if (currentRule != null
								&& currentRule.getTarget() != null) {
							if (!listRule.contains(currentRule)) {
								listRule.add(currentRule);
								currentRule = null;
							}

						} else {
							System.out
									.println("La rule actuelle n'avait pas ete creee !\n");
						}
						break;
					case LF:

						break;
					case DIESE:
						i++;
						while (tabByte[i] != LF) {
							i++;
						}
						break;
					default:
						if (currentRule != null) {
							if (!listRule.contains(currentRule)) {
								listRule.add(currentRule);
							}
						}
						currentRule = new Rule();
						strBuff = new StringBuffer();
						while (tabByte[i] != COLON) {
							strBuff.append((char) tabByte[i]);
							i++;
						}
						currentRule.setTarget(strBuff.toString());
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
								currentRule.getDependencies().add(
										strBuff.toString().trim());
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
		return listRule;
	}

}
