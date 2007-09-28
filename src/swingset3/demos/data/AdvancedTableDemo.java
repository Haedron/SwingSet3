/*
 * Copyright 2007 Sun Microsystems, Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package swingset3.demos.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Document;
import swingset3.DemoProperties;
import swingset3.hyperlink.HyperlinkCellRenderer;

/**
 *
 * @author aim
 */
@DemoProperties(
      value = "Advanced JTable Demo", 
      category = "Data",
      description = "Demonstrates more advanced use of JTable, including asynchronous loading and sorting/filtering.",
      sourceFiles = {
        "sources/swingset3/demos/data/AdvancedTableDemo.java",
        "sources/swingset3/demos/data/OscarCandidate.java",
        "sources/swingset3/demos/data/OscarCellRenderers.java",
        "sources/swingset3/demos/data/OscarTableModel.java",
        "sources/swingset3/demos/data/OscarDataParser.java",
        "sources/swingset3/demos/data/IMDBLink.java"
      }
)
public class AdvancedTableDemo extends JPanel {
    static final Logger logger = Logger.getLogger(AdvancedTableDemo.class.getName());
    
    private OscarTableModel oscarModel;
    
    private JPanel controlPanel;
    private JTable oscarTable;
    private JCheckBox winnersCheckbox;
    private JTextField filterField;
    private Box statusBarLeft;
    private JLabel actionStatus;
    private JLabel tableStatus;
    
    private Color[] rowColors;
    private String statusLabelString;
    private String searchLabelString;
    
    private boolean showOnlyWinners = false;
    private String filterString = null;
    
    private TableRowSorter sorter;
    private RowFilter<OscarTableModel,Integer> winnerFilter;
    private RowFilter<OscarTableModel,Integer> searchFilter;
    
    // Resource bundle for internationalized and accessible text
    private ResourceBundle bundle = null;
    
    public AdvancedTableDemo() { 
        logger.setLevel(Level.FINEST);
        logger.addHandler(new ConsoleHandler());
        initModel();
        initComponents();
        initSortingFiltering();
    } 
    
    protected void initModel() {
         oscarModel = new OscarTableModel();       
    }
                
    protected void initComponents() {   
        setLayout(new BorderLayout());
        
        controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);       
        
        //<snip>Create and initialize JTable
        oscarTable = new JTable(oscarModel);        
        oscarTable.setColumnModel(createColumnModel());
        oscarTable.setAutoCreateRowSorter(true);
        oscarTable.setRowHeight(26);
        oscarTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        oscarTable.setIntercellSpacing(new Dimension(0,0));
        //</snip>
        
        //<snip>Initialize preferred size for table's viewable area
        Dimension viewSize = new Dimension();
        viewSize.width = oscarTable.getColumnModel().getTotalColumnWidth();
        viewSize.height = 10 * oscarTable.getRowHeight();
        oscarTable.setPreferredScrollableViewportSize(viewSize);
        //</snip>
        
        //<snip>Customize height and alignment of table header
        JTableHeader header = oscarTable.getTableHeader();
        header.setPreferredSize(new Dimension(30,26));
        TableCellRenderer headerRenderer = header.getDefaultRenderer();
        if (headerRenderer instanceof JLabel) {
            ((JLabel)headerRenderer).setHorizontalAlignment(JLabel.CENTER);
        }
        //</snip>
        
        JScrollPane scrollpane = new JScrollPane(oscarTable);
        add(BorderLayout.CENTER, scrollpane);
        
        add(BorderLayout.SOUTH, createStatusBar());
        
    }
    
    protected JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c  = new GridBagConstraints();
        controlPanel.setLayout(gridbag);
        
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 3;
        c.insets = new Insets(0,10,0,10);
        c.anchor = GridBagConstraints.SOUTH;
        JLabel oscarStatue = new JLabel(new ImageIcon(AdvancedTableDemo.class.getResource(
                                                     "resources/images/oscar_statue.png")));
        controlPanel.add(oscarStatue, c);                
        
        c.gridx = 1;
        c.gridy = 1;
        c.gridheight = 1;
        c.insets = new Insets(20,0,0,10);
        c.anchor = GridBagConstraints.SOUTHWEST;
        JLabel searchLabel = new JLabel(getString("AdvancedTableDemo.searchLabel",
                                                  "Search Titles and Recipients"));
        controlPanel.add(searchLabel, c);
        
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1.0;
        c.insets.top = 0;
        c.insets.bottom = 12;
        c.anchor = GridBagConstraints.SOUTHWEST;
        //c.fill = GridBagConstraints.HORIZONTAL;
        filterField = new JTextField(24);        
        filterField.getDocument().addDocumentListener(new SearchFilterListener());
        controlPanel.add(filterField, c);
        
        c.gridx = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
        //c.insets.right = 24;
        //c.insets.left = 12;
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        winnersCheckbox = new JCheckBox(getString("AdvancedTableDemo.winnersLabel",
                                                  "Show Only Winners"));
        winnersCheckbox.addChangeListener(new ShowWinnersListener());
        controlPanel.add(winnersCheckbox, c);
        
        return controlPanel;
    }
    
    protected Container createStatusBar() {
        statusLabelString = getString("AdvancedTableDemo.rowCountLabel",
                                      "Showing ");
        searchLabelString = getString("AdvancedTableDemo.searchCountLabel",
                                      "Search found ");
        
        Box statusBar = Box.createHorizontalBox();
        
        // Left status area
        statusBar.add(Box.createRigidArea(new Dimension(10,22)));
        statusBarLeft = Box.createHorizontalBox();
        statusBar.add(statusBarLeft);
        actionStatus = new JLabel(getString("AdvancedTableDemo.noDataStatusLabel",
                                            "No data loaded"));
        actionStatus.setHorizontalAlignment(JLabel.LEADING);        
        statusBarLeft.add(actionStatus);
        
        // Middle (should stretch)
        statusBar.add(Box.createHorizontalGlue());
        statusBar.add(Box.createHorizontalGlue());
        statusBar.add(Box.createVerticalGlue());
        
        // Right status area
        tableStatus = new JLabel(statusLabelString + "0");
        statusBar.add(tableStatus);
        statusBar.add(Box.createHorizontalStrut(12));
        
        oscarModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                // Get rowCount from *table*, not model, as the view row count
                // may be different from the model row count
                tableStatus.setText((hasFilterString()? searchLabelString : statusLabelString) + 
                        oscarTable.getRowCount());
            }
        });        
        
        return (Container)statusBar;
    }

    public Color[] getTableRowColors() {
        if (rowColors == null) {
            rowColors = new Color[2];
            rowColors[0] = UIManager.getColor("Table.background");
            rowColors[1] = new Color((int)(rowColors[0].getRed()*.9),
                    (int)(rowColors[0].getGreen()*.9),
                    (int)(rowColors[0].getBlue()*.9));
        }
        return rowColors;
    }
    
    // returns appropriate string from resource bundle
    protected String getString(String key, String fallback) {
        String value = fallback;
        if (bundle == null) {
            String bundleName = getClass().getPackage().getName()+".resources."+getClass().getSimpleName();
            bundle = ResourceBundle.getBundle(bundleName);
        }
        try {
            value = bundle != null? bundle.getString(key) : key;
            
        } catch (MissingResourceException ex) {
            logger.log(Level.WARNING, "couldn't find resource value for: " + key, ex);
        }
        return value;
    }
    
    public void start() {
        if (oscarModel.getRowCount() == 0) {
            loadData("resources/oscars.xml");
        }
    }
    
    //<snip>Initialize table columns
    protected TableColumnModel createColumnModel() {
        DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
        
        TableCellRenderer cellRenderer = new OscarCellRenderers.RowRenderer(getTableRowColors());
        TableCellRenderer filmEdgeRenderer = new OscarCellRenderers.FilmEdgeRenderer();
        
        TableColumn column = new TableColumn();
        column = new TableColumn();
        column.setModelIndex(OscarTableModel.YEAR_COLUMN);
        column.setHeaderValue(getString("AdvancedTableDemo.yearColumnTitle", "Year"));
        column.setPreferredWidth(26);
        column.setCellRenderer(new OscarCellRenderers.YearRenderer(getTableRowColors()));
        columnModel.addColumn(column);
        
        column = new TableColumn();
        column.setModelIndex(OscarTableModel.CATEGORY_COLUMN);
        column.setHeaderValue(getString("AdvancedTableDemo.categoryColumnTitle", "Award Category"));
        column.setPreferredWidth(100);
        column.setCellRenderer(cellRenderer);
        columnModel.addColumn(column);
        
        column = new TableColumn();
        column.setModelIndex(OscarTableModel.MOVIE_COLUMN);
        column.setHeaderValue(getString("AdvancedTableDemo.movieTitleColumnTitle", "Movie Title"));
        column.setPreferredWidth(180);
        HyperlinkCellRenderer hyperlinkRenderer = 
                new OscarCellRenderers.MovieRenderer(new IMDBLinkAction(), 
                true, getTableRowColors());
        hyperlinkRenderer.setRowColors(getTableRowColors());
        column.setCellRenderer(hyperlinkRenderer);
        columnModel.addColumn(column);
        
        column = new TableColumn();
        column.setModelIndex(OscarTableModel.PERSONS_COLUMN);
        column.setHeaderValue(getString("AdvancedTableDemo.nomineesColumnTitle", "Nominees"));
        column.setPreferredWidth(120);
        column.setCellRenderer(new OscarCellRenderers.NomineeRenderer(getTableRowColors()));
        columnModel.addColumn(column);

        return columnModel;        
    }
    //</snip>
    
    protected void initSortingFiltering() {
        sorter = new TableRowSorter(oscarModel);
        oscarTable.setRowSorter(sorter);
        winnerFilter = new RowFilter<OscarTableModel,Integer>() {
            public boolean include(Entry<? extends OscarTableModel, ? extends Integer> entry) {
                OscarTableModel oscarModel = entry.getModel();
                OscarCandidate candidate = oscarModel.getCandidate(entry.getIdentifier().intValue());
                if (candidate.isWinner()) {
                    // Returning true indicates this row should be shown.
                    return true;
                }
                // loser
                return false;
            }
        };
        searchFilter = new RowFilter<OscarTableModel,Integer>() {
            public boolean include(Entry<? extends OscarTableModel, ? extends Integer> entry) {
                OscarTableModel oscarModel = entry.getModel();
                OscarCandidate candidate = oscarModel.getCandidate(entry.getIdentifier().intValue());
                boolean matches = false;                
                Pattern p = Pattern.compile(filterString+".*", Pattern.CASE_INSENSITIVE);
                
                String movie = candidate.getMovieTitle();
                if (movie != null) {
                    if (movie.startsWith("The ")) {
                        movie = movie.replace("The ", "");
                    } else if (movie.startsWith("A ")) {
                        movie = movie.replace("A ", "");
                    }
                    // Returning true indicates this row should be shown.
                    matches = p.matcher(movie).matches();
                }
                List<String> persons = candidate.getPersons();
                for(String person: persons) {
                    if (p.matcher(person).matches()) {
                        matches = true;
                    }
                }
                return matches;
            }
        };
    }
    
    public void setShowOnlyWinners(boolean showOnlyWinners) {
        boolean oldShowOnlyWinners = this.showOnlyWinners;
        this.showOnlyWinners = showOnlyWinners;
        configureFilters();
        tableStatus.setText(statusLabelString + oscarTable.getRowCount());
        firePropertyChange("showOnlyWinners", oldShowOnlyWinners, showOnlyWinners);
    }
    
    public boolean getShowOnlyWinners() {
        return showOnlyWinners;
    }
    
    public void setFilterString(String filterString) {
        String oldFilterString = this.filterString;
        this.filterString = filterString;
        configureFilters();
        firePropertyChange("filterString", oldFilterString, filterString);
    }
    
    protected boolean hasFilterString() {
        return filterString != null && !filterString.equals("");
    }
    
    protected void configureFilters() {
        if (showOnlyWinners && hasFilterString()) {
            List<RowFilter<OscarTableModel,Integer>> filters = 
                    new ArrayList<RowFilter<OscarTableModel,Integer>>(2);
            filters.add(winnerFilter);
            filters.add(searchFilter);
            RowFilter<Object,Object> comboFilter = RowFilter.andFilter(filters);
            sorter.setRowFilter(comboFilter);
        } else if (showOnlyWinners) {
            sorter.setRowFilter(winnerFilter);
        } else if (hasFilterString()) {
            sorter.setRowFilter(searchFilter);
        } else {
            sorter.setRowFilter(null);
        }
        tableStatus.setText((hasFilterString()? searchLabelString : statusLabelString) 
               + oscarTable.getRowCount());

    }
    
    protected class ShowWinnersListener implements ChangeListener {
        public void stateChanged(ChangeEvent event) {
            setShowOnlyWinners(winnersCheckbox.isSelected());            
        }
    }
    
    protected class SearchFilterListener implements DocumentListener  {
        protected void changeFilter(DocumentEvent event) {
            Document document = event.getDocument();
            try {
                setFilterString(document.getText(0, document.getLength()));
                
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println(ex);
            }
        }
        public void changedUpdate(DocumentEvent e) {
            changeFilter(e);
        }
        public void insertUpdate(DocumentEvent e) {
            changeFilter(e);
        }
        public void removeUpdate(DocumentEvent e) {
            changeFilter(e);
        }
    }
    
    //<snip>Use SwingWorker to asynchronously load the data    
    public void loadData(String dataPath) {
        // create SwingWorker which will load the data on a separate thread
        OscarDataLoader loader = new OscarDataLoader(
                AdvancedTableDemo.class.getResource(dataPath), oscarModel, 10);
        
        actionStatus.setText(getString("AdvancedTableDemo.loadingStatusLabel",
                                       "Loading data: "));
        // display progress bar while data loads
        final JProgressBar progressBar = new JProgressBar();
        statusBarLeft.add(progressBar);
        loader.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals("progress")) {
                    int progress = ((Integer)event.getNewValue()).intValue();
                    progressBar.setValue(progress);
                    if (progress == 100) {
                        statusBarLeft.remove(progressBar);
                        actionStatus.setText("");
                        revalidate();
                    }
                }                    
            }
        });
        loader.execute();
            
    }
    //</snip>
    
    protected void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    //<snip>Use SwingWorker to asynchronously load the data
    class OscarDataLoader extends javax.swing.SwingWorker<List<OscarCandidate>, List<OscarCandidate>> {
        private URL oscarData;
        private OscarTableModel oscarModel;
        private int chunkSize;
        private ArrayList candidates;
        private int candidateCount = 0;
        
        OscarDataLoader(URL oscarURL, OscarTableModel oscarTableModel, int chunkSize) {
            this.oscarData = oscarURL;
            this.oscarModel = oscarTableModel;
            this.chunkSize = chunkSize;
            this.candidates = new ArrayList<OscarCandidate>(8500);
        }
        
        @Override
        public List<OscarCandidate> doInBackground() {
            OscarDataParser parser = new OscarDataParser() {
                @Override
                protected void addCandidate(OscarCandidate candidate) {
                    candidates.add(candidate);
                    logger.log(Level.FINEST, "adding candidate: " + candidate.getCategory() +
                            ": " + candidate.getMovieTitle());
                    if (candidates.size() == chunkSize) {
                        // slow it down to display asynchronous nature of loading
                        // obviously you wouldn't do this in a real application!
                        try {
                            Thread.sleep(5);
                        } catch (Exception ex) {
                            // ignore
                        }
                        publish(candidates);
                        candidateCount+=candidates.size();
                        setProgress(100 * candidateCount / /*8430 remind:parser hangs?!*/ 8330);
                        candidates = new ArrayList<OscarCandidate>();
                    }
                }
            };
            parser.parseDocument(oscarData);
            return candidates;            
        }
        
        @Override
        protected void process(List<OscarCandidate>... moreCandidates) {
            for(List<OscarCandidate> newCandidates: moreCandidates) {
                oscarModel.add(newCandidates);
            }
        }
        
        @Override
        protected void done() {
            try {
                List<OscarCandidate>lastFew = get();
                if (!lastFew.isEmpty()) {                    
                    oscarModel.add(lastFew);
                    candidateCount += lastFew.size();
                    setProgress(100);
                }
            } catch (Exception ignore) {
            }
        }
        
    }
    //</snip>

    public class IMDBLinkAction extends AbstractAction {

        public void actionPerformed(ActionEvent event) {
            int row = ((Integer)event.getSource()).intValue();
            OscarCandidate candidate = oscarModel.getCandidate(oscarTable.convertRowIndexToModel(row));

            try {
                URI imdbURI = candidate.getIMDBMovieURI();
                if (imdbURI == null) {
                    String imdbString = IMDBLink.getMovieURIString(candidate.getMovieTitle(), 
                            candidate.getYear());
                    System.out.println("IMDB="+imdbString);
                    if (imdbString != null) {
                        imdbURI = new URI(imdbString);
                        candidate.setIMDBMovieURI(imdbURI);
                    }
                }
                if (imdbURI != null) {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.browse(imdbURI);
                    
                } else {                    
                    showMessage("IMDB Link",
                            getString("AdvancedTableDemo.imdbLinkNotFound",
                                      "Unable to locate IMDB URL for") + "\n" + 
                            candidate.getMovieTitle());
                }
                        
            } catch (Exception ex) {
                ex.printStackTrace();
            }            
        }
    }
    
    public static void main(String args[]) {
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("JTable Demo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                AdvancedTableDemo demo = new AdvancedTableDemo();
                frame.add(demo);
                frame.setSize(700,400);
                frame.setVisible(true);
                demo.start();
            }
        });
    }
}