package cbit.vcell.client.laf;

import cbit.vcell.client.laf.VCellFonts.Category;
import org.intellij.lang.annotations.MagicConstant;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * A titled border whose title can use one of {@link VCellFonts}' categories, read as it paints, so the title follows
 * font size and theme changes. (A plain TitledBorder given a font keeps that font; given none, it follows the theme's
 * titled border font, which is fine for ordinary titles.) Without a category it is an ordinary TitledBorder.
 */
public class CategoryTitledBorder extends TitledBorder {
	private Category category = null;
	private int style;

	public CategoryTitledBorder(String title) {
		super(title);
	}

	public CategoryTitledBorder(Border border) {
		super(border);
	}

	public CategoryTitledBorder(Border border, String title) {
		super(border, title);
	}


	public CategoryTitledBorder(
			Border border,
			String title,
			@MagicConstant(flags = {
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.LEFT,
					TitledBorder.CENTER,
					TitledBorder.RIGHT,
					TitledBorder.LEADING,
					TitledBorder.TRAILING
			}) int justification,
			@MagicConstant(flags = {
					TitledBorder.DEFAULT_POSITION,
					TitledBorder.ABOVE_TOP,
					TitledBorder.TOP,
					TitledBorder.BELOW_TOP,
					TitledBorder.ABOVE_BOTTOM,
					TitledBorder.BOTTOM,
					TitledBorder.BELOW_BOTTOM}
			) int position
	) {
		super(border, title, justification, position);
	}

	public CategoryTitledBorder(
			Border border,
			String title,
			@MagicConstant(flags = {
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.LEFT,
					TitledBorder.CENTER,
					TitledBorder.RIGHT,
					TitledBorder.LEADING,
					TitledBorder.TRAILING
			}) int justification,
			@MagicConstant(flags = {
					TitledBorder.DEFAULT_POSITION,
					TitledBorder.ABOVE_TOP,
					TitledBorder.TOP,
					TitledBorder.BELOW_TOP,
					TitledBorder.ABOVE_BOTTOM,
					TitledBorder.BOTTOM,
					TitledBorder.BELOW_BOTTOM}
			) int position,
			Font titleFont) {
		super(border, title, justification, position, titleFont);
	}

	public CategoryTitledBorder(
			Border border,
			String title,
			@MagicConstant(flags = {
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.LEFT,
					TitledBorder.CENTER,
					TitledBorder.RIGHT,
					TitledBorder.LEADING,
					TitledBorder.TRAILING
			}) int justification,
			@MagicConstant(flags = {
					TitledBorder.DEFAULT_POSITION,
					TitledBorder.ABOVE_TOP,
					TitledBorder.TOP,
					TitledBorder.BELOW_TOP,
					TitledBorder.ABOVE_BOTTOM,
					TitledBorder.BOTTOM,
					TitledBorder.BELOW_BOTTOM}
			) int position,
			Font titleFont,
			Color titleColor
	) {
		super(border, title, justification, position, titleFont, titleColor);
	}

	public CategoryTitledBorder(Border border, String title, Category category) {
		this(border, title, LEADING, DEFAULT_POSITION, category);
	}

	public CategoryTitledBorder(
			Border border,
			String title,
			@MagicConstant(flags = {
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.LEFT,
					TitledBorder.CENTER,
					TitledBorder.RIGHT,
					TitledBorder.LEADING,
					TitledBorder.TRAILING
			}) int justification,
			@MagicConstant(flags = {
					TitledBorder.DEFAULT_POSITION,
					TitledBorder.ABOVE_TOP,
					TitledBorder.TOP,
					TitledBorder.BELOW_TOP,
					TitledBorder.ABOVE_BOTTOM,
					TitledBorder.BOTTOM,
					TitledBorder.BELOW_BOTTOM}
			) int position,
			Category category
	) {
		super(border, title, justification, position);
		this.setTitleCategory(category);
	}

	public CategoryTitledBorder(
			Border border,
			String title,
			@MagicConstant(flags = {
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.LEFT,
					TitledBorder.CENTER,
					TitledBorder.RIGHT,
					TitledBorder.LEADING,
					TitledBorder.TRAILING
			}) int justification,
			@MagicConstant(flags = {
					TitledBorder.DEFAULT_POSITION,
					TitledBorder.ABOVE_TOP,
					TitledBorder.TOP,
					TitledBorder.BELOW_TOP,
					TitledBorder.ABOVE_BOTTOM,
					TitledBorder.BOTTOM,
					TitledBorder.BELOW_BOTTOM}
			) int position,
			Category category,
			int style
	) {
		super(border, title, justification, position);
		this.setTitleCategory(category, style);
	}

	/** Titles in this kind of text from now on, following size and theme changes. */
	public void setTitleCategory(Category category) {
		this.setTitleCategory(category, VCellFonts.getFont(category).getStyle());
	}

	/** As {@link #setTitleCategory(Category)}, in another style (e.g. an italic title). */
	public void setTitleCategory(Category category, int style) {
		this.category = category;
		this.style = style;
	}

	@Override
	public Font getTitleFont() {
		return null == this.category ? super.getTitleFont() : VCellFonts.getFont(this.category, this.style);
	}

	@Override
	public void setTitleFont(Font titleFont) {
		this.category = null; // a fixed font replaces the category
		super.setTitleFont(titleFont);
	}
}
