package net.schwarzbaer.java.lib.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ValueRange;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings("unused")
public class TimeInput extends JPanel
{
	private static final long serialVersionUID = -3907472266293924160L;

	private final ValueField yearField;
	private final ValueField monthField;
	private final ValueField dayField;
	private final ValueField hourField;
	private final ValueField minField;
	private final ValueField secField;
	
	private final ZonedDateTime initValue;
	private ZonedDateTime value;
	private JLabel resultOutput;

	public TimeInput(ZonedDateTime initValue)
	{
		super(new GridBagLayout());
		this.initValue = initValue;
		this.value = initValue;
		setPreferredSize(new Dimension(400,130));
		
		yearField  = new ValueField(ChronoField.YEAR            , this::getValue, this::setValue);
		monthField = new ValueField(ChronoField.MONTH_OF_YEAR   , this::getValue, this::setValue);
		dayField   = new ValueField(ChronoField.DAY_OF_MONTH    , this::getValue, this::setValue, ()->ChronoField.DAY_OF_MONTH.rangeRefinedBy(value));
		hourField  = new ValueField(ChronoField.HOUR_OF_DAY     , this::getValue, this::setValue);
		minField   = new ValueField(ChronoField.MINUTE_OF_HOUR  , this::getValue, this::setValue);
		secField   = new ValueField(ChronoField.SECOND_OF_MINUTE, this::getValue, this::setValue);
		resultOutput = new JLabel(toString(value));
		resultOutput.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(3,10,3,10)));
	
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		c.weightx = 0;
		int x = 0, y = 0;
		c.anchor = GridBagConstraints.EAST;
		add(c, x++,0, 1,1, new JLabel("Date :  "));
		c.anchor = GridBagConstraints.CENTER;
		
		x = addField(c, x, y,   dayField, "D");
		x = addField(c, x, y, monthField, "M");
		x = addField(c, x, y,  yearField, "Y");
		
		x = 0; y = 2;
		c.anchor = GridBagConstraints.EAST;
		add(c, x++,y+0, 1,1, new JLabel("Time :  "));
		c.anchor = GridBagConstraints.CENTER;
		
		x = addField(c, x, y, hourField, "H");
		x = addField(c, x, y,  minField, "M");
		x = addField(c, x, y,  secField, "S");
		
		x = 0; y = 4;
		c.weightx = 0;
		c.anchor = GridBagConstraints.EAST;
		add(c, x++,y+0, 1,1, new JLabel("Result :  "));
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 1;
		add(c, x  ,y+0, 10,1, resultOutput);
		x+=10;
	}

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy, HH:mm:ss ( VV, z, O )");
	private static String toString(ZonedDateTime value)
	{
		return formatter.format(value);
//		return value.format(formatter);
//		return OpenWebifController.dateTimeFormatter.getTimeStr(value.getLong(ChronoField.INSTANT_SECONDS)*1000, Locale.ENGLISH, true, true, false, true, true);
	}

	private int addField(GridBagConstraints c, int x, int y, ValueField field, String label)
	{
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0;
		add(c, x++,y+0, 1,1, new JLabel(String.format("  %s ", label)));
		c.weightx = 1;
		add(c, x  ,y+0, 2,1, field.valueField);
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1;
		c.anchor = GridBagConstraints.EAST;
		add(c, x++,y+1, 1,1, field.decBtn);
		c.anchor = GridBagConstraints.WEST;
		add(c, x++,y+1, 1,1, field.incBtn);
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
//		c.weightx = 1;
//		add(c, x  ,y+0, 1,1, new JLabel(""));
//		add(c, x++,y+1, 1,1, new JLabel(""));
		return x;
	}
	
	private void add(GridBagConstraints c, int gridx, int gridy, int gridwidth, int gridheight, Component comp)
	{
		c.gridwidth  = gridwidth ;
		c.gridheight = gridheight;
		c.gridx      = gridx     ;
		c.gridy      = gridy     ;
		add(comp, c);
		//System.out.printf("[%d,%d,%d,%d] %s%n", gridx, gridy, gridwidth, gridheight, comp.getClass().getSimpleName());
	}
	
	public ZonedDateTime getValue()
	{
		return value;
	}
	
	public void setValue(ZonedDateTime newValue)
	{
		value = newValue;
		yearField .updateValue();
		monthField.updateValue();
		dayField  .updateValue();
		hourField .updateValue();
		minField  .updateValue();
		secField  .updateValue();
		resultOutput.setText(toString(value));
	}

	private static class ValueField
	{
		private final ChronoField field;
		private final Supplier<ZonedDateTime> getValue;
		private final Consumer<ZonedDateTime> setValue;
		private final Supplier<ValueRange> getRange;
		private final ValueRange range;
		
		private final JTextField valueField;
		private final JButton decBtn;
		private final JButton incBtn;
		
		ValueField(ChronoField field, Supplier<ZonedDateTime> getValue, Consumer<ZonedDateTime> setValue) { this(field, getValue, setValue, null); }
		ValueField(ChronoField field, Supplier<ZonedDateTime> getValue, Consumer<ZonedDateTime> setValue, Supplier<ValueRange> getRange)
		{
			this.field = field;
			this.getValue = getValue;
			this.setValue = setValue;
			this.getRange = getRange;
			this.range = this.getRange==null ? this.field.range() : null;
			
			valueField = new JTextField(Integer.toString(getValue()), 5);
			valueField.setHorizontalAlignment(JTextField.CENTER);
			valueField.setPreferredSize(new Dimension(50,25));
			valueField.setMinimumSize(new Dimension(50,25));
			valueField.addActionListener(e->{
				long n;
				try { n = Long.parseLong(valueField.getText()); }
				catch (NumberFormatException ex) { n = getValue(); }
				
				ValueRange range = this.range;
				if (range==null) range = this.getRange.get();
				
				long n_ = Math.min(Math.max( range.getMinimum(), n ), range.getMaximum() );
				changeValue(val->val.with(this.field, n_));
			});
			
			decBtn = createButton(new Dimension(15,15), new Insets(0, 0, 0, 0), "-", e->changeValue(val->val.minus(1, this.field.getBaseUnit())));
			incBtn = createButton(new Dimension(15,15), new Insets(0, 0, 0, 0), "+", e->changeValue(val->val.plus (1, this.field.getBaseUnit())));
		}
		
		private static JButton createButton(Dimension size, Insets margin, String text, ActionListener al2)
		{
			JButton comp = new JButton(text);
			comp.addActionListener(al2);
			comp.setMargin(margin);
			comp.setPreferredSize(size);
			comp.setMinimumSize(size);
			return comp;
		}
		
		private void changeValue(Function<ZonedDateTime,ZonedDateTime> changeAction)
		{
			ZonedDateTime oldValue = getValue.get();
			ZonedDateTime newValue = changeAction.apply(oldValue);
			setValue.accept(newValue);
		}
		
		void updateValue()
		{
			valueField.setText(Integer.toString(getValue()));
		}
		
		private int getValue()
		{
			return getValue.get().get(field);
		}
		
	}
}
