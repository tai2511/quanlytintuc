package Control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Dto.FileData;
import Dto.Request;
import Dto.Response;
import Dto.SearchCategoryForm;
import Dto.SearchNewsForm;
import Model.CategoryModel;
import Model.NewsModel;

public class ServerControl {
	private Connection con;
	private ServerSocket myServer;
	private int serverPort = 7777;

	public ServerControl() {
		// connect to database my SQL
		getDBConnection("laptrinhmang", "root", "");

		// open server to accept connect from client
		openServer(serverPort);
		while (true) {
			// accept connect from client, receive request fron client and send response to
			// client
			listenning();
		}

	}

	private void getDBConnection(String dbName, String username, String password) {
		String dbUrl = "jdbc:mysql://127.0.0.1:3306/" + dbName + "?serverTimezone=UTC";
		String dbClass = "com.mysql.cj.jdbc.Driver";
		try {
			Class.forName(dbClass);
			con = DriverManager.getConnection(dbUrl, username, password);
			if (con == null)
				System.out.println("Ket noi khong thanh cong");
			else
				System.out.println("Ket noi DB thanh cong");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void openServer(int portNumber) {
		try {
			myServer = new ServerSocket(portNumber);
			if (myServer == null)
				System.out.println("Khong mo dc");
			else
				System.out.println("Mo cong thanh cong ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void listenning() {
		try {
			Socket clientSocket = myServer.accept();
			System.out.println(clientSocket.getRemoteSocketAddress() + " connected\n");
			ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

			Request o = (Request) ois.readObject();
			Response response = new Response();
			switch (o.getAction()) {
			case UPLOADIMAGE:
				response = uploadImage(o);
				break;
			case ALLCATEGORY:
				response = getAllCategoryFromDB();
				break;
			case ADDCATEGORY:
				response = addCategory2DB(o);
				break;
			case UPDATECATEGORY:
				response = updateCategory2DB(o);
				break;
			case REMOVECATEGORY:
				response = removeCategoryFromDB(o);
				break;
			case GETCATEGORYCODENAME:
				response = getCategoryCodeName();
				break;
			case ALLNEWS:
				response = getAllNewsFromDB();
				break;
			case ADDNEWS:
				response = addNews2DB(o);
				break;
			case UPDATENEWS:
				response = updateNews2DB(o);
				break;
			case REMOVENEWS:
				response = removeNewsFromDB(o);
				break;
			case SEARCHNEWS:
				response = searchNewsFromDB(o);
				break;
			case SEARCHCATEGORY:
				response = searchCategoryFromDB(o);
				break;
			case REPORTCATEGORY:
				response = getCategoryReport();
				break;
			}
			oos.writeObject(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Response uploadImage(Request request) throws IOException {
		FileData fileData = (FileData) request.getData();
		String fileName = fileData.getFileName();
		byte[] fileContent = fileData.getFileContent();

		File imagesDir = new File("images");
		if (!imagesDir.exists()) {
			imagesDir.mkdir();
		}

		File file = new File(imagesDir, fileName);
		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			fileOutputStream.write(fileContent);
		}
		System.out.println("File uploaded: " + file.getAbsolutePath());

		Response res = new Response();
		res.setData(new String(file.getAbsolutePath()));
		return res;
	}

	private Response removeCategoryFromDB(Request request) {
		CategoryModel model = (CategoryModel) request.getData();
		String sql = "DELETE FROM categories WHERE code = ?";
		Boolean success = false;

		try (PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, model.getCode());

			int affectedRows = stmt.executeUpdate();
			success = affectedRows > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			success = false;
		}

		Response res = new Response();
		res.setData(success);
		return res;
	}

	private Response addCategory2DB(Request request) {
		CategoryModel model = (CategoryModel) request.getData();
		String sql = "INSERT INTO categories (code, name, description) VALUES (?, ?, ?)";
		Boolean success = false;

		try (PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, model.getCode());
			stmt.setString(2, model.getName());
			stmt.setString(3, model.getDescription());

			int affectedRows = stmt.executeUpdate();
			success = affectedRows > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			success = false;
		}

		Response res = new Response();
		res.setData(success);
		return res;
	}

	private Response updateCategory2DB(Request request) {
		CategoryModel model = (CategoryModel) request.getData();
		String sql = "UPDATE categories SET name = ?, description = ? where code = ?";
		Boolean success = false;

		try (PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, model.getName());
			stmt.setString(2, model.getDescription());
			stmt.setString(3, model.getCode());

			int affectedRows = stmt.executeUpdate();
			success = affectedRows > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			success = false;
		}

		Response res = new Response();
		res.setData(success);
		return res;
	}

	private Response getAllCategoryFromDB() {
		List<CategoryModel> categories = new ArrayList<>();
		String query = "SELECT * FROM categories";

		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				String code = rs.getString("code");
				String name = rs.getString("name");
				String description = rs.getString("description");

				CategoryModel category = new CategoryModel(code, name, description);
				categories.add(category);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Response res = new Response();
		res.setData(categories);
		return res;
	}

	private Response getCategoryReport() {
		HashMap<String, Integer> data = new HashMap<>();
		String query_2 = "SELECT \r\n" + "    categories.name category_name,\r\n"
				+ "    COUNT(news.code) / news_total.news_total_coloumn * 100 total\r\n" + "FROM\r\n"
				+ "    (SELECT \r\n" + "        COUNT(code) news_total_coloumn\r\n" + "    FROM\r\n"
				+ "        news) news_total,\r\n" + "    news\r\n" + "        LEFT JOIN\r\n"
				+ "    categories ON categories.code = news.category_code\r\n" + "GROUP BY (category_code)";

		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query_2)) {

			while (rs.next()) {
				String name = rs.getString("category_name");
				Integer total = rs.getInt("total");
				data.put(name, total);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Response res = new Response();
		res.setData(data);
		return res;
	}

	private Response getCategoryCodeName() {
		HashMap<String, String> category_arr = new HashMap<>();
		String query_2 = "SELECT code, name FROM categories";

		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query_2)) {

			while (rs.next()) {
				String code = rs.getString("code");
				String name = rs.getString("name");

				category_arr.put(code, name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Response res = new Response();
		res.setData(category_arr);
		return res;
	}

	private Response searchCategoryFromDB(Request request) {
		List<CategoryModel> categories = new ArrayList<>();
		SearchCategoryForm form = (SearchCategoryForm) request.getData();
		String query = "SELECT * FROM categories";
		if (!form.getKeyword().isBlank()) {
			query += " WHERE name like '%" + form.getKeyword() + "%'";
		}

		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				String code = rs.getString("code");
				String name = rs.getString("name");
				String description = rs.getString("description");

				CategoryModel category = new CategoryModel(code, name, description);
				categories.add(category);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Response res = new Response();
		res.setData(categories);
		return res;
	}

	private Response searchNewsFromDB(Request request) {
		List<NewsModel> news = new ArrayList<>();
		SearchNewsForm form = (SearchNewsForm) request.getData();
		String query = "SELECT * FROM news";
		if (!form.getCategoryCode().isBlank() || form.getDate() != null) {
			query += " WHERE ";
			if (form.getCategoryCode().isBlank()) {
				query += " create_date >= '" + form.getDate().toString() + "'";
			} else if (form.getDate() == null) {
				query += " category_code = '" + form.getCategoryCode() + "'";
			} else {
				query += " category_code = '" + form.getCategoryCode() + "' AND " + " create_date >= '"
						+ form.getDate().toString() + "'";
			}
		}

		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				String code = rs.getString("code");
				String title = rs.getString("title");
				String content = rs.getString("content");
				String image = rs.getString("image");
				String categoryCode = rs.getString("category_code");
				LocalDate createDate = rs.getDate("create_date").toLocalDate();

				NewsModel newModel = new NewsModel(code, title, content, image, categoryCode, createDate);
				news.add(newModel);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Response res = new Response();
		res.setData(news);
		return res;
	}

	private Response getAllNewsFromDB() {
		List<NewsModel> news = new ArrayList<>();
		String query = "SELECT * FROM news";

		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				String code = rs.getString("code");
				String title = rs.getString("title");
				String content = rs.getString("content");
				String image = rs.getString("image");
				String categoryCode = rs.getString("category_code");
				LocalDate createDate = rs.getDate("create_date").toLocalDate();

				NewsModel newModel = new NewsModel(code, title, content, image, categoryCode, createDate);
				news.add(newModel);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Response res = new Response();
		res.setData(news);
		return res;
	}

	private Response addNews2DB(Request request) {
		NewsModel model = (NewsModel) request.getData();
		String sql = "INSERT INTO news VALUES (?, ?, ?, ?, ?, CURDATE())";
		Boolean success = false;

		try (PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, model.getCode());
			stmt.setString(2, model.getTitle());
			stmt.setString(3, model.getContent());
			stmt.setString(4, model.getImage());
			stmt.setString(5, model.getCategoryCode());

			int affectedRows = stmt.executeUpdate();
			success = affectedRows > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			success = false;
		}

		Response res = new Response();
		res.setData(success);
		return res;
	}

	private Response updateNews2DB(Request request) {
		NewsModel model = (NewsModel) request.getData();
		String sql = "UPDATE news SET title = ?, content = ?, image = ?, category_code = ? where code = ?";
		Boolean success = false;

		try (PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(5, model.getCode());
			stmt.setString(1, model.getTitle());
			stmt.setString(2, model.getContent());
			stmt.setString(3, model.getImage());
			stmt.setString(4, model.getCategoryCode());

			int affectedRows = stmt.executeUpdate();
			success = affectedRows > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			success = false;
		}

		Response res = new Response();
		res.setData(success);
		return res;
	}

	private Response removeNewsFromDB(Request request) {
		NewsModel model = (NewsModel) request.getData();
		String sql = "DELETE FROM news WHERE code = ?";
		Boolean success = false;

		try (PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, model.getCode());

			int affectedRows = stmt.executeUpdate();
			success = affectedRows > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			success = false;
		}

		Response res = new Response();
		res.setData(success);
		return res;
	}

}
