package jp.co.iccom.shinbori_kenshiro.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class calculate_sales {
	public static void main(String[] args) throws FileNotFoundException {
		HashMap<String, String> branchNames = new HashMap<String, String>();
		HashMap<String, String> commodityNames = new HashMap<String, String>();
		HashMap<String, Long> branchAmount = new HashMap<String, Long>();
		HashMap<String, Long> commodityAmount = new HashMap<String, Long>();

		if (args.length != 1) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		try {
			File file = new File(args[0], "branch.lst");

			if (!file.exists()) {
				System.out.println("支店定義ファイルが存在しません");
				return;
			}

			BufferedReader br = new BufferedReader(new FileReader(file));
			String str;

			while ((str = br.readLine()) != null) {
				String array[] = str.split(",");

				if (array.length != 2) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;

				}
				if (!array[0].matches("^\\d{3}$")) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}
				branchNames.put(array[0], array[1]);
				branchAmount.put(array[0], 0L);

			}

			br.close();

		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;

		} finally {

		}

		try {
			File file = new File(args[0], "commodity.lst");

			if (!file.exists()) {
				System.out.println("商品定義ファイルが存在しません");
				return;
			}

			BufferedReader br = new BufferedReader(new FileReader(file));
			String str;
			while ((str = br.readLine()) != null) {
				String array[] = str.split(",");

				if (array.length != 2) {
					System.out.println("商品定義ファイルのフォーマットが不正です");

					return;

				}
				if (!array[0].matches("^[a-zA-Z0-9]{8}$")) {
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
				commodityNames.put(array[0], array[1]);
				commodityAmount.put(array[0], 0L);
			}
			br.close();
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;

		} finally {

		}

		try {
			ArrayList<File> salesList = new ArrayList<File>();

			File file = new File(args[0]);
			File[] files = file.listFiles();

			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile() && files[i].getName().matches("^[0-9]{8}.rcd$")) {
					salesList.add(files[i]);
				}
			}

			for (int i = 0; i < salesList.size() - 1; i++) {

				String rcdName = new String(salesList.get(i).getName());
				int numRcdName = Integer.parseInt(rcdName.substring(0, 8));
				String rcdName1 = new String(salesList.get(i + 1).getName());
				int numRcdName1 = Integer.parseInt(rcdName1.substring(0, 8));

				if (numRcdName1 - numRcdName != 1) {
					System.out.println("売上ファイル名が連番になっていません");
					return;
				}
			}

			for (int i = 0; i < salesList.size(); i++) {
				ArrayList<String> rcdList = new ArrayList<String>();

				BufferedReader br = new BufferedReader(new FileReader(salesList.get(i)));
				String str;
				while ((str = br.readLine()) != null) {
					rcdList.add(str);
				}

				if (rcdList.size() != 3) {
					System.out.println(salesList.get(i).getName() + "のフォーマットが不正です");
					return;
				}

				try {
					if (!rcdList.get(2).matches("^[0-9]+$")) {
						System.out.println("予期せぬエラーが発生しました");
					} else {
						long rcdvalue = Long.parseLong(rcdList.get(2));

						if (!branchAmount.containsKey(rcdList.get(0))) {
							System.out.println(salesList.get(i).getName() + "の支店コードが不正です");
							return;
						}

						if (!commodityAmount.containsKey(rcdList.get(1))) {
							System.out.println(salesList.get(i).getName() + "の商品コードが不正です");
							return;
						}
						
						rcdvalue = rcdvalue + branchAmount.get(rcdList.get(0));
						if (rcdvalue <= 1000000000) {
							branchAmount.put(rcdList.get(0), rcdvalue);
						} else {
							System.out.println("合計金額が10桁を超えました");
							return;
						}

						rcdvalue = rcdvalue + commodityAmount.get(rcdList.get(1));
						if (rcdvalue <= 1000000000) {
							commodityAmount.put(rcdList.get(1), rcdvalue);
						} else {
							System.out.println("合計金額が10桁を超えました");
							return;
						}
					}
				} finally {
				}
			}

		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;

		} finally {

		}

		List<Map.Entry<String, Long>> branchentries = new ArrayList<Map.Entry<String, Long>>(branchAmount.entrySet());
		Collections.sort(branchentries, new Comparator<Map.Entry<String, Long>>() {

			public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
				return ((Long) o2.getValue()).compareTo((Long) o1.getValue());
			}
		});

		try {
			File branchOutFile = new File(args[0], "branch.out");
			FileWriter fw = new FileWriter(branchOutFile);
			BufferedWriter bw = new BufferedWriter(fw);

			for (Entry<String, Long> s : branchentries) {

				final String separator = System.getProperty("line.separator");

				bw.write(s.getKey() + "," + branchNames.get(s.getKey()) + "," + s.getValue() + separator);

			}
			bw.close();
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;

		} finally {

		}
		List<Map.Entry<String, Long>> commodityentries = new ArrayList<Map.Entry<String, Long>>(
				commodityAmount.entrySet());
		Collections.sort(commodityentries, new Comparator<Map.Entry<String, Long>>() {

			public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
				return ((Long) o2.getValue()).compareTo((Long) o1.getValue());

			}
		});

		try {
			File commodityOutFile = new File(args[0], "commodity.out");
			FileWriter fw = new FileWriter(commodityOutFile);
			BufferedWriter bw = new BufferedWriter(fw);
			for (Entry<String, Long> s : commodityentries) {

				final String separator = System.getProperty("line.separator");

				bw.write(s.getKey() + "," + commodityNames.get(s.getKey()) + "," + s.getValue() + separator);

			}
			bw.close();
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;

		} finally {

		}

	}

}