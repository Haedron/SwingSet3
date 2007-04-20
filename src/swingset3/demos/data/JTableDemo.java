/*
 * JTableDemo.java
 *
 * Created on September 22, 2006, 3:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3.demos.data;

import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author aim
 */
public class JTableDemo extends JPanel {
    
    private Color rowColors[];
    private Color tableFrameColor = new Color(20,20,20);
    
    private JTable oscarTable;
    private OscarTableModel oscarModel;

    // remind: replace with annotation?
    public static String getShortDescription() {
        return "Demonstrates JTable, Swing's component which displays tabular data.";
    }
    
    public JTableDemo() { 
        setToolTipText(getShortDescription());
        initModel();
        initComponents();
    }    
                
    protected void initComponents() {   
        setLayout(new BorderLayout());
        
        oscarTable = new JTable(oscarModel);
        
        rowColors = new Color[2];
        rowColors[0] = new Color(240,240,240);
        rowColors[1] = new Color(230, 230, 230);
        
        oscarTable.setColumnModel(createColumnModel());
        oscarTable.setAutoCreateRowSorter(true);
        oscarTable.setRowHeight(30);
        oscarTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        oscarTable.setIntercellSpacing(new Dimension(0,0));
        
        JTableHeader header = new OscarTableHeader(oscarTable.getColumnModel());
        oscarTable.setTableHeader(header);

        
        Dimension viewSize = new Dimension();
        viewSize.width = oscarTable.getColumnModel().getTotalColumnWidth();
        viewSize.height = 10 * oscarTable.getRowHeight();
        oscarTable.setPreferredScrollableViewportSize(viewSize);
        
        JScrollPane scrollpane = new JScrollPane(oscarTable);
        add(BorderLayout.CENTER, scrollpane);
        
    }
    
    protected TableColumnModel createColumnModel() {
        DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
        TableCellRenderer cellRenderer = new RowCellRenderer();
        TableCellRenderer filmEdgeRenderer = new FilmCellRenderer();
        
        TableColumn column = new TableColumn();
        int width = 34;
        column.setModelIndex(OscarTableModel.YEAR_COLUMN); // dummy
        column.setHeaderValue("");
        column.setCellRenderer(filmEdgeRenderer);
        column.setPreferredWidth(width);
        column.setResizable(false);
        column.setMinWidth(width);
        column.setMaxWidth(width);
        columnModel.addColumn(column);
        
        column = new TableColumn();
        column.setModelIndex(OscarTableModel.YEAR_COLUMN);
        column.setHeaderValue("Year");
        column.setPreferredWidth(30);
        column.setCellRenderer(new YearCellRenderer());
        columnModel.addColumn(column);
        
        column = new TableColumn();
        column.setModelIndex(OscarTableModel.CATEGORY_COLUMN);
        column.setHeaderValue("Award Category");
        column.setPreferredWidth(120);
        column.setCellRenderer(cellRenderer);
        columnModel.addColumn(column);
        
        column = new TableColumn();
        column.setModelIndex(OscarTableModel.MOVIE_COLUMN);
        column.setHeaderValue("Movie");
        column.setPreferredWidth(150);
        column.setCellRenderer(cellRenderer);
        columnModel.addColumn(column);
        
        column = new TableColumn();
        column.setModelIndex(OscarTableModel.PERSONS_COLUMN);
        column.setHeaderValue("Recipients");
        column.setPreferredWidth(120);
        column.setCellRenderer(new RecipientCellRenderer());
        columnModel.addColumn(column);
       
        column = new TableColumn();
        column.setModelIndex(OscarTableModel.YEAR_COLUMN); // dummy
        column.setHeaderValue("");
        column.setCellRenderer(filmEdgeRenderer);
        column.setPreferredWidth(width);
        column.setResizable(false);
        column.setMinWidth(width);
        column.setMaxWidth(width);
        columnModel.addColumn(column);
        
        return columnModel;
        
    }
    
    protected void initModel() {
         OscarDataParser parser = new OscarDataParser();
         oscarModel = new OscarTableModel(
                 parser.parseDocument(JTableDemo.class.getResource("resources/oscars.xml")));
         System.out.println("oscar count:" + oscarModel.getRowCount());
    }
    
    
    private class OscarTableModel extends AbstractTableModel {
        public static final int CATEGORY_COLUMN = 0;
        public static final int YEAR_COLUMN = 1;
        public static final int WINNER_COLUMN = 2;
        public static final int MOVIE_COLUMN = 3;
        public static final int PERSONS_COLUMN = 4;
        public static final int COLUMN_COUNT = 5;
        
        List<OscarCandidate> candidates;
        
        public OscarTableModel(List<OscarCandidate> candidates) {
            super();
            this.candidates = candidates;
        }
        public int getRowCount() {
            return candidates.size();            
        }
        public int getColumnCount() {
            return COLUMN_COUNT;            
        }
        
        public Class getColumnClass(int column) {
            return getValueAt(0, column).getClass();
        }
        
        public Object getValueAt(int row, int column) {
            OscarCandidate oscarCandidate = candidates.get(row);
            switch(column) {
                case CATEGORY_COLUMN: 
                    return oscarCandidate.getCategory();
                case YEAR_COLUMN: 
                    return oscarCandidate.getYear();
                case MOVIE_COLUMN: 
                    return oscarCandidate.getMovie();
                case WINNER_COLUMN:
                    return oscarCandidate.isWinner()? Boolean.TRUE : Boolean.FALSE;
                case PERSONS_COLUMN:
                    List persons = oscarCandidate.getPersons();
                    if (persons.size() > 0) {
                        return oscarCandidate.getPersons().get(0);
                    }
            }
            return null;
        }
        
    }
    

    public class OscarHeaderRenderer extends DefaultTableCellRenderer {
        private Icon sortAscendingIcon;
        private Icon sortDescendingIcon;

        public OscarHeaderRenderer() {
            sortAscendingIcon = new ImageIcon(
                    getClass().getResource("resources/images/sort_ascending.png"));
            sortDescendingIcon = new ImageIcon(
                    getClass().getResource("resources/images/sort_descending.png"));
            setHorizontalAlignment(JLabel.CENTER);
            setHorizontalTextPosition(JLabel.LEADING);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Icon sortIcon = null;
            
            boolean isPaintingForPrint = false;
            
            if (table != null) {
                JTableHeader header = table.getTableHeader();
                if (header != null) {  
                    setForeground(header.getForeground());
                    //setBackground(tableFrameColor);   
                    setOpaque(false);
                    setFont(header.getFont());                   
                    isPaintingForPrint = header.isPaintingForPrint();
                }
                
                if (!isPaintingForPrint && table.getRowSorter() != null) {
                    
                    java.util.List<? extends RowSorter.SortKey> sortKeys =
                            table.getRowSorter().getSortKeys();
                    if (sortKeys.size() > 0 && sortKeys.get(0).getColumn() ==
                            table.convertColumnIndexToModel(column)) {
                        switch(sortKeys.get(0).getSortOrder()) {
                            case ASCENDING:
                                sortIcon = sortAscendingIcon;
                                break;
                            case DESCENDING:
                                sortIcon = sortDescendingIcon;
                                break;
                            case UNSORTED:
                                sortIcon = UIManager.getIcon("Table.naturalSortIcon");
                                break;
                        }
                    }
                }
            }
            
            setText(((value == null) || (value == "")) ? " " : value.toString());
            setIcon(sortIcon);
            
            
            return this;
        }
        
    }
    
    public class OscarTableHeader extends JTableHeader {
        private int gradientRamp = 50;
        
        public OscarTableHeader(TableColumnModel columnModel) {
            super(columnModel);
            setOpaque(false);
            setForeground(Color.white);
            setPreferredSize(new Dimension(100, 30));
            setDefaultRenderer(new OscarHeaderRenderer());
            
        }
        
        public void paint(Graphics g) {
            Rectangle bounds = getBounds();

            Graphics2D g2 = (Graphics2D)g.create();
            
            Color dark = tableFrameColor;
            Color lighter = new Color(dark.getRed() + gradientRamp, 
                                      dark.getGreen() + gradientRamp, 
                                      dark.getBlue() + gradientRamp);
            
            System.out.println("red="+dark.getRed()+" green="+dark.getGreen()+" blue="+dark.getBlue());
            GradientPaint gradient = new GradientPaint(0, 0, dark,
                                                       0, bounds.height/2, lighter,
                                                       false);
            g2.setPaint(gradient);
            //g2.fillRect(1, 0, bounds.width-2, bounds.height/2);
            
            gradient = new GradientPaint(0, 0/*bounds.height/2*/, lighter,
                                         0, bounds.height, dark,
                                         false);
            g2.setPaint(gradient);
            g2.fillRect(1, 0/*bounds.height/2*/, bounds.width-2, bounds.height/*/2*/);
            super.paint(g);
            
        }

    }

     public class RowCellRenderer extends DefaultTableCellRenderer {
                
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
                        
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(rowColors[row % rowColors.length]);
            return this;            
        }
        
    }
     
    public class YearCellRenderer extends RowCellRenderer {
        HashMap<String,Font> eraFonts = new HashMap(); 
        
        public YearCellRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
     
            eraFonts.put("192"/*1920's*/, new Font("Jazz LET", Font.PLAIN, 12));
            eraFonts.put("193"/*1930's*/, new Font("Mona Lisa Solid ITC TT", Font.BOLD, 18));
            eraFonts.put("194"/*1940's*/, new Font("American Typewriter", Font.BOLD, 12));
            eraFonts.put("195"/*1950's*/, new Font("Britannic Bold", Font.PLAIN, 12));
            eraFonts.put("196"/*1960's*/, new Font("Cooper Black", Font.PLAIN, 14));
            eraFonts.put("197"/*1970's*/, new Font("Syncro LET", Font.PLAIN, 14));
            eraFonts.put("198"/*1980's*/, new Font("Mistral", Font.PLAIN, 18));
            eraFonts.put("199"/*1990's*/, new Font("Papyrus", Font.BOLD, 14));
            eraFonts.put("200"/*2000's*/, new Font("Calisto MT", Font.PLAIN, 14));
            
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
           
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String year = (String)table.getValueAt(row, 
                    table.convertColumnIndexToView(OscarTableModel.YEAR_COLUMN));
            if (year != null && year.length() == 4) {
                String era = ((String)year).substring(0, 3);
                Font eraFont = eraFonts.get(era);
                setFont(eraFont);
            }
            return this;
        }
        
    }

    public class CategoryCellRenderer extends RowCellRenderer {
        HashMap<OscarCandidate.Category,String> candidateTitles = new HashMap(); 
        
        public CategoryCellRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
     
            candidateTitles.put(OscarCandidate.Category.BEST_ACTOR, "BEST_ACTOR");
            candidateTitles.put(OscarCandidate.Category.BEST_ACTRESS, "BEST ACTRESS");
            
         
            
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
           
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            String category = candidateTitles.get((OscarCandidate.Category)value);
            setText(category);
            return this;
        }
        
    }
    
    public class FilmCellRenderer extends RowCellRenderer {
        private ImageIcon filmEdgeIcon;
        
        public FilmCellRenderer() {
            filmEdgeIcon = new ImageIcon(
                    getClass().getResource("resources/images/filmchad.jpg"));
            setBackground(tableFrameColor);
        }        
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            if (column == 0) {
                setHorizontalAlignment(JLabel.LEFT);
            } else {
                setHorizontalAlignment(JLabel.RIGHT);
            }
            setText("");
            setIcon(filmEdgeIcon);            
            return this;                     
        }               
    }
    
    public class RecipientCellRenderer extends RowCellRenderer {
        private ImageIcon winnerIcon;
        
        private boolean isWinner;
        
        public RecipientCellRenderer() {
            winnerIcon = new ImageIcon(
                    getClass().getResource("resources/images/goldstar.png"));
            
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            TableModel model = table.getModel();
            isWinner = ((Boolean)model.getValueAt(table.convertRowIndexToModel(row),
                                        OscarTableModel.WINNER_COLUMN)).booleanValue();
            setText(value != null? value.toString() : "");            
            setIcon(isWinner? winnerIcon : null);
            setHorizontalTextPosition(JLabel.TRAILING);

            return this;            
        }
        
        
        
    }
    
    public static void main(String args[]) {
        final JTableDemo demo = new JTableDemo();
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("JTable Demo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(demo);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}