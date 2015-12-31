package homework.tools;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jboss.logging.Logger;

import homework.utils.Crypter;
import homework.utils.EMProducer;
import homework.utils.SystemException;

/**
 * エクセルに記載したデータをテーブルに登録する。
 * @author satake
 */
public class ExcelDataImporter {

	/** ロガー */
	private Logger logger = Logger.getLogger(ExcelDataImporter.class);

	/**
	 * エクセルを読み込みデータをテーブルに登録する。
	 * @param manager	エンティティマネジャー
	 * @param xlsFile	エクセルファイル名
	 */
	public void importRecords(EntityManager manager, String xlsFile) {
		try {
			// クラスパスからファイル名を探してワークブックを取得する
			Workbook book = WorkbookFactory.create(this.getClass().getClassLoader().getResourceAsStream(xlsFile));
			// シート名（テーブル名）の一覧を取得する
			List<Sheet> sheets = new ArrayList<>();
			for (int sheetNo = 0; sheetNo < book.getNumberOfSheets(); ++sheetNo) {
				Sheet sheet = book.getSheetAt(sheetNo);
				if (sheet.getSheetName().startsWith("#")) {
					logger.info("シート名が「#」で始まっているためスキップします。 - " + sheet.getSheetName());
					continue;
				}
				sheets.add(sheet);
			}
			// データを削除する
			for (int i = sheets.size() - 1; i >= 0; i--) {
				Sheet sheet = sheets.get(i);
				deleteTable(manager, sheet.getSheetName());
			}
			// データを登録する
			for (Sheet sheet : sheets) {
				String tableName = sheet.getSheetName();
				logger.info(tableName + "シートの処理を開始します。");
				List<String> columns = getColumns(sheet);
				insertRecords(manager, sheet, columns);
				dumpTable(manager, tableName);
			}
		}
		catch (Exception e) {
			logger.error(e);
			throw new SystemException(e);
		}
	}

	/**
	 * テーブルデータを削除する。
	 * @param manager	エンティティマネジャー
	 * @param tableName
	 */
	private void deleteTable(EntityManager manager, String tableName) {
		logger.info(tableName + "テーブルのデータを削除します。");
		String sql = "DELETE FROM " + tableName;
		logger.debug(sql);
		int deleteCount = manager.createNativeQuery(sql).executeUpdate();
		logger.info(deleteCount + "件削除しました。");
	}

	/**
	 * カラム名を取得する。
	 * @param sheet		エクセルシート
	 * @return	カラム名のリスト
	 */
	private List<String> getColumns(Sheet sheet) {
		List<String> columns = new ArrayList<>();
		Row row = sheet.getRow(0);
		for (int colNo = 0; colNo < Short.MAX_VALUE; colNo++) {
			Cell cell = row.getCell(colNo);
			if (cell == null) {
				break;
			}
			columns.add(cell.getStringCellValue());
		}
		return columns;
	}

	/**
	 * シートのレコードデータを登録する。
	 * @param manager	エンティティマネジャー
	 * @param sheet		エクセルシート
	 * @param columns	カラム名のリスト
	 */
	private void insertRecords(EntityManager manager, Sheet sheet, List<String> columns) {
		int insertCount = 0;
		boolean isUserTable = StringUtils.equals(sheet.getSheetName().toUpperCase(), "USERS");
		for (int rowNo = 1; rowNo < Short.MAX_VALUE; rowNo++) {
			Row row = sheet.getRow(rowNo);
			if (row == null) {
				break;
			}
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO ").append(sheet.getSheetName()).append(" (");
			for (String column : columns) {
				sql.append(column).append(",");
			}
			sql.delete(sql.length() - 1, sql.length());
			sql.append(") VALUES (");
			for (int colNo = 0; colNo < columns.size(); colNo++) {
				Cell cell = row.getCell(colNo);
				if (cell != null) {
					if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						sql.append(cell.getNumericCellValue()).append(",");
					}
					else {
						sql.append("'");
						if (isUserTable && StringUtils.equals(columns.get(colNo).toUpperCase(), "PASSWORD")) {
							sql.append(Crypter.encrypt(cell.getStringCellValue()));
						}
						else {
							sql.append(cell.getStringCellValue());
						}
						sql.append("',");
					}
				}
				else {
					sql.append("null,");
				}
			}
			sql.delete(sql.length() - 1, sql.length());
			sql.append(")");
			logger.debug(sql);
			manager.createNativeQuery(sql.toString()).executeUpdate();
			insertCount++;
		}
		logger.info(insertCount + "件登録しました。");
	}

	/**
	 * テーブルの内容をダンプする。
	 * @param manager	エンティティマネジャー
	 * @param tableName	テーブル名
	 */
	private void dumpTable(EntityManager manager, String tableName) {
		String sql = "SELECT * FROM " + tableName;
		logger.debug("------------------------------------------------------------------------------");
		logger.debug("★ table dump: " + tableName);
		logger.debug("------------------------------------------------------------------------------");
		List<?> result = manager.createNativeQuery(sql).getResultList();
		StringBuilder dump = new StringBuilder();
		for (Object record : result) {
			dump.append(ReflectionToStringBuilder.toString(record, ToStringStyle.MULTI_LINE_STYLE)).append("\n");
		}
		logger.debug(dump);
		logger.debug("------------------------------------------------------------------------------");
	}

	public static void main(String[] args) {
		// log4jdbcの設定
		System.setProperty("log4jdbc.spylogdelegator.name", "net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator");
		System.setProperty("org.slf4j.simpleLogger.log.jdbc", "ERROR");
		System.setProperty("org.slf4j.simpleLogger.log.jdbc.sqltiming", "INFO");
		System.setProperty("org.slf4j.simpleLogger.log.jdbc.resultsettable", "INFO");

		EntityManager manager = EMProducer.createManager();
		try {
			ExcelDataImporter importer = new ExcelDataImporter();
			manager.getTransaction().begin();
			importer.importRecords(manager, "InitialData.xlsx");
			manager.getTransaction().commit();
		}
		catch (Exception e) {
			manager.getTransaction().rollback();
			e.printStackTrace();
			System.exit(-1);
		}
		System.exit(0);
	}
}
