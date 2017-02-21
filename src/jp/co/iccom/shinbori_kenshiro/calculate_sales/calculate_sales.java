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
		
		input(args[0],"branch.lst" ,branchNames ,branchAmount,"^\\d{3}$");
		input(args[0],"commodity.lst" ,commodityNames ,commodityAmount ,"^[a-zA-Z0-9]{8}$");
		
		try {
			ArrayList<File> salesList = new ArrayList<File>();

			File file = new File(args[0]);
			File[] files = file.
					listFiles();

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
						return;
					}
					long rcdvalue = Long.parseLong(rcdList.get(2));

					if (!branchAmount.containsKey(rcdList.get(0))) {
						System.out.println(salesList.get(i).getName() + "の支店コードが不正です");
						return;
					}

					if (!commodityAmount.containsKey(rcdList.get(1))) {
						System.out.println(salesList.get(i).getName() + "の商品コードが不正です");
						return;
					}

					long amountvalue = rcdvalue + branchAmount.get(rcdList.get(0));
					if (amountvalue <= 1000000000) {
						branchAmount.put(rcdList.get(0), amountvalue);
					} else {
						System.out.println("合計金額が10桁を超えました");
						return;
					}

					long commodityvalue = rcdvalue + commodityAmount.get(rcdList.get(1));
					if (commodityvalue <= 1000000000) {
						commodityAmount.put(rcdList.get(1), commodityvalue);
					} else {
						System.out.println("合計金額が10桁を超えました");
						return;
					}
				} finally {
				}
			}

		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;

		} finally {

		}
		
		
		output(args[0] ,"branch.out" ,branchNames , branchAmount );
		output(args[0] ,"commodity.out" ,commodityNames ,commodityAmount);
		
		
	}



	public static boolean output(String path ,String fileName ,HashMap<String,String> mapNames ,HashMap<String,Long> mapAmount){


		List<Map.Entry<String, Long>> entries = new ArrayList<Map.Entry<String, Long>>(mapAmount.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<String, Long>>() {

			public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
				return ((Long) o2.getValue()).compareTo((Long) o1.getValue());
			}
		});

		BufferedWriter bw =null;
		try {
			File file = new File(path, fileName);
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

			for (Entry<String, Long> s : entries) {
				bw.write(s.getKey() + "," + mapNames.get(s.getKey()) + "," + s.getValue() + System.getProperty("line.separator"));
			}
			
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}finally{
			try{
				if(bw !=null){
					bw.close();
				}
			} catch (IOException e) {
					e.printStackTrace();
			}
		}
		return true;
	}
	
	
	
	public static boolean input(String path ,String fileName ,HashMap<String,String> mapNames ,HashMap<String,Long> mapAmount ,String word){
		
		
		BufferedReader br = null;
		try {
			File file = new File(path, fileName);

			if (!file.exists()) {
				System.out.println("支店定義ファイルが存在しません");
				return false;
			}
			br = new BufferedReader(new FileReader(file));
			String str;

			while ((str = br.readLine()) != null) {
				String array[] = str.split(",");

				if (array.length != 2) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return false;

				}
				if (!array[0].matches(word)) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return false;
				}
				mapNames.put(array[0], array[1]);
				mapAmount.put(array[0], 0L);
			}
			
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return false;

			} finally {
				try{
					if(br !=null){
						br.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		return true;
		
	}

}
