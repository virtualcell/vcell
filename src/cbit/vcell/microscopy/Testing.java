package cbit.vcell.microscopy;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JButton;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicSliderUI;
 
class Testing extends JFrame
{
  private JList list;
  public Testing()
  {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocation(200,100);
    final JSlider slider = new JSlider(0,100,0);
    slider.setMajorTickSpacing(10);
    slider.setMinorTickSpacing(5);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    JPanel jp = new JPanel();
    jp.add(slider);
    getContentPane().setLayout(null);
    getContentPane().add(slider);
    
    slider.setUI(new MyUI(slider));

    final JButton nextButton = new JButton();
    nextButton.setText("Next");
    nextButton.setEnabled(false);
    nextButton.setBounds(120, 278, 81, 26);
    getContentPane().add(nextButton);

    final JButton nextButton_1 = new JButton();
    nextButton_1.setText("Finish");
//    nextButton_1.setEnabled(false);
    nextButton_1.setBounds(216, 278, 81, 26);
    getContentPane().add(nextButton_1);

    final JRadioButton cropRadioButton = new JRadioButton();
    cropRadioButton.setText("crop");
    cropRadioButton.setEnabled(false);
//    cropRadioButton.setSelected(true);
    cropRadioButton.setBounds(21, 73, 51, 24);
    getContentPane().add(cropRadioButton);

    final JLabel label = new JLabel();
    label.setText("--->");
    label.setBounds(78, 77, 27, 16);
    getContentPane().add(label);

    final JRadioButton defineCellRoiRadioButton = new JRadioButton();
    defineCellRoiRadioButton.addActionListener(new ActionListener() {
    	public void actionPerformed(final ActionEvent arg0) {
    	}
    });
    defineCellRoiRadioButton.setText("Cell ROI");
    defineCellRoiRadioButton.setEnabled(false);
//    defineCellRoiRadioButton.setSelected(true);
    defineCellRoiRadioButton.setBounds(99, 73, 74, 24);
    getContentPane().add(defineCellRoiRadioButton);

    final JRadioButton defineBleachedRoiRadioButton = new JRadioButton();
    defineBleachedRoiRadioButton.addActionListener(new ActionListener() {
    	public void actionPerformed(final ActionEvent e) {
    	}
    });
    defineBleachedRoiRadioButton.setText("Bleached ROI");
//    defineBleachedRoiRadioButton.setSelected(true);
    defineBleachedRoiRadioButton.setEnabled(false);
    defineBleachedRoiRadioButton.setBounds(199, 75, 101, 20);
    getContentPane().add(defineBleachedRoiRadioButton);

    final JLabel label_1 = new JLabel();
    label_1.setText("--->");
    label_1.setBounds(178, 77, 27, 16);
    getContentPane().add(label_1);

    final JLabel label_2 = new JLabel();
    label_2.setText("--->");
    label_2.setBounds(299, 77, 66, 16);
    getContentPane().add(label_2);

    final JRadioButton representativeBackgroundRadioButton = new JRadioButton();
    representativeBackgroundRadioButton.setText("Representative Background");
    representativeBackgroundRadioButton.setSelected(true);
//    representativeBackgroundRadioButton.setEnabled(false);
    representativeBackgroundRadioButton.setBounds(317, 73, 191, 24);
    getContentPane().add(representativeBackgroundRadioButton);

    final JButton chooseButton = new JButton();
    chooseButton.setText("");
    chooseButton.setBounds(21, 155, 66, 60);
    getContentPane().add(chooseButton);

    final JButton button = new JButton();
    button.setText("New JButton");
    button.setEnabled(false);
    button.setBounds(99, 155, 66, 60);
    getContentPane().add(button);

    final JRadioButton pureDiffusionRadioButton = new JRadioButton();
    pureDiffusionRadioButton.setActionCommand("ROI_Cell");
    pureDiffusionRadioButton.setText("ROI_Cell");
//    pureDiffusionRadioButton.setSelected(true);
    pureDiffusionRadioButton.setBounds(31, 241, 87, 24);
    getContentPane().add(pureDiffusionRadioButton);

    final JRadioButton reactionDiffusionRadioButton = new JRadioButton();
    reactionDiffusionRadioButton.setText("ROI_Background");
//    reactionDiffusionRadioButton.setEnabled(false);
    reactionDiffusionRadioButton.setBounds(269, 241, 177, 24);
    getContentPane().add(reactionDiffusionRadioButton);

    final JButton viewSpatialResultsButton = new JButton();
    viewSpatialResultsButton.setText("Compare Models");
    viewSpatialResultsButton.setBounds(21, 329, 184, 26);
    getContentPane().add(viewSpatialResultsButton);

    final JRadioButton oneRadioButton = new JRadioButton();
    oneRadioButton.setText("Pure Diffusion");
    oneRadioButton.setSelected(true);
    oneRadioButton.setBounds(190, 143, 129, 24);
    getContentPane().add(oneRadioButton);

    final JRadioButton twoRadioButton = new JRadioButton();
    twoRadioButton.setText("Reaction plus Diffusion");
//    twoRadioButton.setSelected(true);
    twoRadioButton.setBounds(191, 173, 174, 24);
    getContentPane().add(twoRadioButton);

    final JButton nextButton_2 = new JButton();
    nextButton_2.setMargin(new Insets(2, 6, 2, 6));
    nextButton_2.setText("Previous");
//    nextButton_2.setEnabled(false);
    nextButton_2.setBounds(26, 278, 81, 26);
    getContentPane().add(nextButton_2);

    final JCheckBox checkBox_1 = new JCheckBox();
    checkBox_1.setText("Diffusion plus Binding");
    checkBox_1.setSelected(true);
    checkBox_1.setBounds(299, 490, 177, 20);
    getContentPane().add(checkBox_1);

    final JCheckBox roi_bleachedCheckBox = new JCheckBox();
    roi_bleachedCheckBox.setText("ROI_Bleached");
    roi_bleachedCheckBox.setBounds(299, 310, 118, 20);
    getContentPane().add(roi_bleachedCheckBox);

    final JCheckBox roi_ring1CheckBox = new JCheckBox();
    roi_ring1CheckBox.setText("ROI_Ring1");
    roi_ring1CheckBox.setBounds(299, 330, 118, 20);
    getContentPane().add(roi_ring1CheckBox);

    final JCheckBox roi_ring2CheckBox = new JCheckBox();
    roi_ring2CheckBox.setText("ROI_Ring2");
    roi_ring2CheckBox.setBounds(299, 350, 118, 20);
    getContentPane().add(roi_ring2CheckBox);

    final JCheckBox checkBox_7 = new JCheckBox();
    checkBox_7.setText("New JCheckBox");
    checkBox_7.setBounds(299, 370, 118, 20);
    getContentPane().add(checkBox_7);

    final JCheckBox checkBox_8 = new JCheckBox();
    checkBox_8.setText("New JCheckBox");
    checkBox_8.setBounds(299, 390, 118, 20);
    getContentPane().add(checkBox_8);

    final JCheckBox checkBox_9 = new JCheckBox();
    checkBox_9.setText("Pure Diffusion with One Diffuing Component (Analytic Half Cell)");
    checkBox_9.setBounds(299, 410, 377, 20);
    getContentPane().add(checkBox_9);

    final JCheckBox checkBox_10 = new JCheckBox();
    checkBox_10.setText("Pure Diffusin with One Diffuing Component (Analytic Guassian)");
    checkBox_10.setBounds(299, 430, 407, 20);
    getContentPane().add(checkBox_10);

    final JCheckBox checkBox_11 = new JCheckBox();
    checkBox_11.setText("Pure Diffusion with One Diffusing Component");
//    checkBox_11.setEnabled(false);
    checkBox_11.setSelected(true);
    checkBox_11.setBounds(299, 450, 361, 20);
    getContentPane().add(checkBox_11);

    final JCheckBox checkBox_12 = new JCheckBox();
    checkBox_12.setText("Pure Diffusion with Two Diffusing Component");
    checkBox_12.setSelected(true);
    checkBox_12.setBounds(299, 470, 287, 20);
    getContentPane().add(checkBox_12);

    final JRadioButton roi_bleachedRadioButton = new JRadioButton();
    roi_bleachedRadioButton.setSelected(true);
    roi_bleachedRadioButton.setText("ROI_Bleached");
    roi_bleachedRadioButton.setBounds(136, 241, 130, 24);
    getContentPane().add(roi_bleachedRadioButton);

    final JButton cancelButton = new JButton();
    cancelButton.setText("Cancel");
    cancelButton.setBounds(127, 367, 81, 26);
    getContentPane().add(cancelButton);

    final JButton sButton = new JButton();
    sButton.setText("Add");
    sButton.setBounds(383, 142, 87, 26);
    getContentPane().add(sButton);

    list = new JList();
    list.setBounds(21, 431, 201, 240);
    list.setBorder(new EtchedBorder());
    getContentPane().add(list);

    final JButton sButton_1 = new JButton();
    sButton_1.setText("delete");
    sButton_1.setBounds(389, 209, 87, 26);
    getContentPane().add(sButton_1);
    pack();
  }
  class MyUI extends BasicSliderUI
  {
    public MyUI(JSlider js)
    {
      super(js);
    }
    protected void scrollDueToClickInTrack(int dir)
    {
      //do nothing
    }
  }
  public static void main(String[] args){new Testing().setVisible(true);}
}
