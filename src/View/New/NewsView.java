package View.New;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.github.lgooddatepicker.components.DatePicker;

import Control.ClientControl;
import Dto.ActionName;
import Dto.FileData;
import Dto.Request;
import Dto.Response;
import Dto.SearchNewsForm;
import Model.NewsModel;

public class NewsView extends JPanel {

	private JPanel detailListPanel;
	private static JScrollPane newsPanel;
	private JButton addButton;
	private JButton cancelButton;
	private JTextField codeInput;
	private JTextField titleInput;
	private JTextArea contentInput;
	private JLabel imageLabel;
	private JTextField imageUri;
	private JComboBox<String> categoryComboBox;
	private HashMap<String, String> category_arr = getCategoryCodeName();

	public NewsView() {
		detailListPanel = new JPanel();
		detailListPanel.setLayout(new BorderLayout());
		createNewsForm();
		newsPanel = loadNews(getAllNews());
		detailListPanel.add(newsPanel, BorderLayout.CENTER);
		createNewsSearch();
		add(detailListPanel);
	}

	public JScrollPane loadNews(List<NewsModel> news) {

		// Content
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		JPanel headerPanel = new JPanel(new GridLayout(1, 6));
		headerPanel.setPreferredSize(new Dimension(800, 40));
		headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		headerPanel.add(new JLabel("Ảnh", JLabel.LEFT));
		headerPanel.add(new JLabel("Mã", JLabel.LEFT));
		headerPanel.add(new JLabel("Tiêu đề", JLabel.LEFT));
		headerPanel.add(new JLabel("Nội dung", JLabel.LEFT));
		headerPanel.add(new JLabel("Thể loại", JLabel.LEFT));
		headerPanel.add(new JLabel("Ngày tạo", JLabel.LEFT));
		headerPanel.setBackground(Color.LIGHT_GRAY);

		content.add(headerPanel);

		for (NewsModel newItem : news) {

			JPopupMenu popupMenu = new JPopupMenu();
			JMenuItem editItem = new JMenuItem("Cập nhật");
			JMenuItem deleteItem = new JMenuItem("Xóa");
			popupMenu.setPopupSize(120, 50);
			popupMenu.add(editItem);
			popupMenu.addSeparator();
			popupMenu.add(deleteItem);
			JPanel rowPanel = new JPanel(new GridLayout(1, 6));
			if (!newItem.getImage().isBlank()) {
				ImageIcon icon = new ImageIcon(newItem.getImage());
				Image img = icon.getImage();

				Image scaledImage = img.getScaledInstance(100, 50, Image.SCALE_SMOOTH);

				ImageIcon scaledIcon = new ImageIcon(scaledImage);
				rowPanel.add(new JLabel(scaledIcon));
			} else {
				rowPanel.add(new JLabel(""));
			}

			rowPanel.add(new JLabel(newItem.getCode()));
			rowPanel.add(new JLabel(newItem.getTitle()));
			rowPanel.add(new JLabel(newItem.getContent()));

			String categoryCode = newItem.getCategoryCode();
			String categoryname = category_arr.get(categoryCode);
			rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
			rowPanel.setPreferredSize(new Dimension(800, 55));
			rowPanel.add(new JLabel(categoryname));
			rowPanel.add(new JLabel(newItem.getCreateDate().toString()));
			rowPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
			rowPanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.isPopupTrigger()) {
						showPopup(e);
					}
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.isPopupTrigger()) {
						showPopup(e);
					}
				}

				private void showPopup(MouseEvent e) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			});

			editItem.addActionListener(editAction(newItem.getCode(), newItem.getTitle(), newItem.getContent(),
					newItem.getImage(), newItem.getCategoryCode()));
			deleteItem.addActionListener(deleteAction(newItem.getCode()));
			content.add(rowPanel);
		}
		newsPanel = new JScrollPane(content);
		newsPanel.setPreferredSize(new Dimension(900, 340));
		newsPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		newsPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		return newsPanel;
	}

	private ActionListener editAction(String code, String title, String content, String imageLink,
			String categoryCode) {
		return e -> {
			addButton.setText("Cập nhật");
			cancelButton.setVisible(true);
			codeInput.setText(code);
			codeInput.setEnabled(false);
			titleInput.setText(title);
			contentInput.setText(content);

			if (imageLink.isEmpty()) {
				imageLabel.setIcon(null);
			} else {
				ImageIcon icon = new ImageIcon(imageLink);
				Image img = icon.getImage();
				Image scaledImage = img.getScaledInstance(100, 50, Image.SCALE_SMOOTH);
				ImageIcon scaledIcon = new ImageIcon(scaledImage);
				imageLabel.setIcon(scaledIcon);
			}
			imageUri.setText(imageLink);

			HashMap<String, String> category_arr = getCategoryCodeName();
			categoryComboBox.setSelectedItem(category_arr.get(categoryCode));
		};
	}

	private ActionListener deleteAction(String id) {
		return e -> {
			Request request = new Request();
			request.setAction(ActionName.REMOVENEWS);
			request.setData(new NewsModel(id, null, null, null, null, null));
			ClientControl clientCtr = new ClientControl();
			Response result = clientCtr.connectAndGetData(request);
			Boolean status = (Boolean) result.getData();
			if (status) {
				JOptionPane.showMessageDialog(null, "Xóa thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
				detailListPanel.remove(newsPanel);
				detailListPanel.add(loadNews(getAllNews()));
				detailListPanel.revalidate();
				detailListPanel.repaint();
			} else {
				JOptionPane.showMessageDialog(null, "Có lỗi xảy ra. Vui lòng thử lại sau!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		};
	}

	private void createNewsForm() {
		JPanel block = new JPanel();
		block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));

		JPanel blockImage = new JPanel(new FlowLayout());
		imageLabel = new JLabel();
		blockImage.add(imageLabel);

		imageUri = new JTextField();
		imageUri.setVisible(false);
		JButton uploadButton = new JButton("Thêm ảnh");
		blockImage.add(uploadButton);
		blockImage.setAlignmentX(Component.LEFT_ALIGNMENT);

		block.add(blockImage);

		uploadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Chọn 1 ảnh");
				fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg",
						"jpeg", "png", "gif"));

				int userSelection = fileChooser.showOpenDialog(block);
				if (userSelection == JFileChooser.APPROVE_OPTION) {
					File fileToUpload = fileChooser.getSelectedFile();
					ImageIcon icon = new ImageIcon(fileToUpload.getAbsolutePath());
					Image img = icon.getImage();
					Image scaledImage = img.getScaledInstance(100, 50, Image.SCALE_SMOOTH);
					ImageIcon scaledIcon = new ImageIcon(scaledImage);
					imageLabel.setIcon(scaledIcon);
					String image_uri = (String) uploadFile(fileToUpload).getData();
					imageUri.setText(image_uri);
				}
			}
		});
		JLabel label1 = new JLabel("Mã:");
		label1.setAlignmentX(Component.LEFT_ALIGNMENT);
		block.add(label1);
		codeInput = new JTextField();
		codeInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		codeInput.setPreferredSize(new Dimension(200, 30));
		codeInput.setAlignmentX(Component.LEFT_ALIGNMENT);
		block.add(codeInput);

		JLabel label2 = new JLabel("Tiêu đề:");
		label2.setAlignmentX(Component.LEFT_ALIGNMENT);
		block.add(label2);
		titleInput = new JTextField();
		titleInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		titleInput.setPreferredSize(new Dimension(200, 30));
		titleInput.setAlignmentX(Component.LEFT_ALIGNMENT);
		block.add(titleInput);

		JLabel label3 = new JLabel("Nội dung:");
		label3.setAlignmentX(Component.LEFT_ALIGNMENT);
		block.add(label3);
		contentInput = new JTextArea();
		contentInput.setPreferredSize(new Dimension(200, 100));
		contentInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
		contentInput.setAlignmentX(Component.LEFT_ALIGNMENT);
		block.add(contentInput);

		JLabel label4 = new JLabel("Thể loại:");
		label4.setAlignmentX(Component.LEFT_ALIGNMENT);
		block.add(label4);
		Collection<String> values = category_arr.values();
		String[] s1 = values.toArray(new String[0]);
		categoryComboBox = new JComboBox<String>(s1);
		categoryComboBox.setPreferredSize(new Dimension(200, 20));
		categoryComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		categoryComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		block.add(categoryComboBox);

		JPanel buttonBlock = new JPanel(new FlowLayout());
		addButton = new JButton("Thêm");
		addButton.setPreferredSize(new Dimension(100, 30));
		addButton.addActionListener(storeNews(imageUri, codeInput, titleInput, contentInput, categoryComboBox));
		cancelButton = new JButton("Hủy bỏ");
		cancelButton.setPreferredSize(new Dimension(100, 30));
		cancelButton.setVisible(false);
		cancelButton.addActionListener(e -> {
			resetForm();
		});
		buttonBlock.add(addButton);
		buttonBlock.add(cancelButton);
		buttonBlock.setAlignmentX(Component.LEFT_ALIGNMENT);
		block.add(buttonBlock);
		detailListPanel.add(block, BorderLayout.NORTH);

	}

	private void createNewsSearch() {
		JPanel block = new JPanel();
		block.setLayout(new FlowLayout(FlowLayout.LEFT));
		block.add(new JLabel("Tìm kiếm theo: "));

		block.add(new JLabel("Thể loại:"));
		Collection<String> values = category_arr.values();
		String[] s1 = values.toArray(new String[0]);
		categoryComboBox = new JComboBox<String>(s1);
		block.add(categoryComboBox);

		block.add(new JLabel("Đăng sau ngày:"));
		DatePicker datePicker = new DatePicker();
		block.add(datePicker);

		JButton btnSearch = new JButton();
		btnSearch.setText("Tìm kiếm");
		block.add(btnSearch);
		btnSearch.addActionListener(serachNews(categoryComboBox, datePicker));
		detailListPanel.add(block, BorderLayout.SOUTH);
	}

	private ActionListener serachNews(JComboBox<String> categoryComboBox, DatePicker datePicker) {
		return e -> {
			Request request = new Request();
			request.setAction(ActionName.SEARCHNEWS);

			String selected = (String) categoryComboBox.getSelectedItem();
			String categoryCode = getKeyFromValue(category_arr, selected);

			System.out.println(datePicker.getDate());

			request.setData(new SearchNewsForm(categoryCode, datePicker.getDate()));
			ClientControl clientCtr = new ClientControl();
			Response result = clientCtr.connectAndGetData(request);
			List<NewsModel> news = (List<NewsModel>) result.getData();
			if (news.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Không có kết quả tìm kiếm", "Thông báo",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				detailListPanel.remove(newsPanel);
				detailListPanel.add(loadNews(news));
				detailListPanel.revalidate();
				detailListPanel.repaint();
			}
		};
	}

	private ActionListener storeNews(JTextField imageUri, JTextField codeInput, JTextField nameInput,
			JTextArea descriptionInput, JComboBox<String> categoryCb) {
		return e -> {
			Request request = new Request();
			if (codeInput.isEnabled()) {
				request.setAction(ActionName.ADDNEWS);
			} else {
				request.setAction(ActionName.UPDATENEWS);
			}

			String selected = (String) categoryCb.getSelectedItem();
			String categoryCode = getKeyFromValue(category_arr, selected);

			request.setData(new NewsModel(codeInput.getText(), nameInput.getText(), descriptionInput.getText(),
					imageUri.getText(), categoryCode, null));
			ClientControl clientCtr = new ClientControl();
			Response result = clientCtr.connectAndGetData(request);
			Boolean status = (Boolean) result.getData();
			if (status) {
				JOptionPane.showMessageDialog(null, "Thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
				detailListPanel.remove(newsPanel);
				detailListPanel.add(loadNews(getAllNews()));
				detailListPanel.revalidate();
				detailListPanel.repaint();
			} else {
				JOptionPane.showMessageDialog(null, "Có lỗi xảy ra. Vui lòng thử lại sau!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
			resetForm();
		};
	}

	private void resetForm() {
		codeInput.setText("");
		titleInput.setText("");
		contentInput.setText("");
		addButton.setText("Thêm");
		cancelButton.setVisible(false);
		codeInput.setEnabled(true);
		imageLabel.setIcon(null);
		imageUri.setText("");
		categoryComboBox.setSelectedIndex(0);
	}

	private static HashMap<String, String> getCategoryCodeName() {
		Request request = new Request();
		request.setAction(ActionName.GETCATEGORYCODENAME);
		ClientControl clientCtr = new ClientControl();
		Response result = clientCtr.connectAndGetData(request);
		return (HashMap<String, String>) result.getData();
	}

	private List<NewsModel> getAllNews() {
		Request request = new Request();
		request.setAction(ActionName.ALLNEWS);
		ClientControl clientCtr = new ClientControl();
		Response result = clientCtr.connectAndGetData(request);
		return (List<NewsModel>) result.getData();
	}

	public static String getKeyFromValue(HashMap<String, String> map, String value) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}
		return null;
	}

	private Response uploadFile(File file) {
		Response result = null;
		try (FileInputStream fileInputStream = new FileInputStream(file);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = fileInputStream.read(buffer)) != -1) {
				byteArrayOutputStream.write(buffer, 0, bytesRead);
			}

			FileData fileData = new FileData(file.getName(), file.length(), byteArrayOutputStream.toByteArray());

			Request request = new Request();
			request.setAction(ActionName.UPLOADIMAGE);
			request.setData(fileData);
			ClientControl clientCtr = new ClientControl();
			result = clientCtr.connectAndGetData(request);
		} catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to upload image.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		return result;
	}
}
