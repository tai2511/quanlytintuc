/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import View.New.NewsView;
import View.Category.Category;


public class ClientView extends JFrame implements ActionListener {
	private JTree tree;
	private JPanel contentPanel;
	private DefaultMutableTreeNode categoryNode;

	public ClientView() {
		setTitle("News");
		setSize(1400, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		createSidebar();

		contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(new JLabel("Chọn mục từ sidebar để hiển thị nội dung"), BorderLayout.CENTER);
		contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		add(contentPanel, BorderLayout.CENTER);

		setVisible(true);
	}

	private void createSidebar() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Menu");

		DefaultMutableTreeNode newsNode = new DefaultMutableTreeNode("Tin tức");
		DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode("Thể loại");

		root.add(newsNode);
		root.add(categoryNode);

		tree = new JTree(root);

		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (selectedNode == null)
					return;
				String selectedNodeName = selectedNode.toString();
				updateContent(selectedNodeName);
			}
		});

		JScrollPane treeScrollPane = new JScrollPane(tree);
		treeScrollPane.setPreferredSize(new Dimension(250, 0));

		add(treeScrollPane, BorderLayout.WEST);
	}

	private void updateContent(String selectedNodeName) {
		contentPanel.removeAll();

		if (selectedNodeName.equals("Tin tức")) {
			contentPanel.add(new NewsView(), BorderLayout.NORTH);
		} else if (selectedNodeName.equals("Thể loại")) {
			contentPanel.add(new Category(), BorderLayout.NORTH);
		} else {
			contentPanel.add(new JLabel("Chọn mục từ sidebar để hiển thị nội dung"), BorderLayout.CENTER);
		}

		contentPanel.revalidate();
		contentPanel.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Xử lý sự kiện khi người dùng tương tác với giao diện
	}

	// Hàm main để khởi chạy chương trình
	public static void main(String[] args) {
		new ClientView();
	}
}