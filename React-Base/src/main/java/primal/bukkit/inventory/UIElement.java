package primal.bukkit.inventory;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.volmit.react.util.Protocol;

import primal.bukkit.world.MaterialBlock;
import primal.compute.math.M;
import primal.lang.collection.GList;

public class UIElement implements Element
{
	private MaterialBlock material;
	private boolean enchanted;
	private final String id;
	private String name;
	private double progress;
	private boolean bg;
	private final GList<String> lore;
	private Callback<Element> eLeft;
	private Callback<Element> eRight;
	private Callback<Element> eShiftLeft;
	private Callback<Element> eShiftRight;
	private Callback<Element> eDraggedInto;
	private Callback<Element> eOtherDraggedInto;
	private int count;

	public UIElement(String id)
	{
		this.id = id;
		lore = new GList<>();
		enchanted = false;
		count = 1;
		material = new MaterialBlock(Material.AIR);
	}

	@Override
	public MaterialBlock getMaterial()
	{
		return material;
	}

	@Override
	public UIElement setMaterial(MaterialBlock material)
	{
		try
		{
			if(Protocol.R1_13.to(Protocol.LATEST).contains(Protocol.getProtocolVersion()))
			{
				material.setData((byte) 0);
			}
		}
		
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		this.material = material;
		return this;
	}

	@Override
	public boolean isEnchanted()
	{
		return enchanted;
	}

	@Override
	public UIElement setEnchanted(boolean enchanted)
	{
		this.enchanted = enchanted;
		return this;
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public UIElement setName(String name)
	{
		this.name = name;
		return this;
	}

	@Override
	public GList<String> getLore()
	{
		return lore;
	}

	@Override
	public UIElement onLeftClick(Callback<Element> clicked)
	{
		eLeft = clicked;
		return this;
	}

	@Override
	public UIElement onRightClick(Callback<Element> clicked)
	{
		eRight = clicked;
		return this;
	}

	@Override
	public UIElement onShiftLeftClick(Callback<Element> clicked)
	{
		eShiftLeft = clicked;
		return this;
	}

	@Override
	public UIElement onShiftRightClick(Callback<Element> clicked)
	{
		eShiftRight = clicked;
		return this;
	}

	@Override
	public UIElement onDraggedInto(Callback<Element> into)
	{
		eDraggedInto = into;
		return this;
	}

	@Override
	public UIElement onOtherDraggedInto(Callback<Element> other)
	{
		eOtherDraggedInto = other;
		return this;
	}

	@Override
	public Element call(ElementEvent event, Element context)
	{
		try
		{
			switch(event)
			{
				case DRAG_INTO:
					eDraggedInto.run(context);
					return this;
				case LEFT:
					eLeft.run(context);
					return this;
				case OTHER_DRAG_INTO:
					eOtherDraggedInto.run(context);
					return this;
				case RIGHT:
					eRight.run(context);
					return this;
				case SHIFT_LEFT:
					eShiftLeft.run(context);
					return this;
				case SHIFT_RIGHT:
					eShiftRight.run(context);
					return this;
			}
		}

		catch(NullPointerException e)
		{

		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return this;
	}

	@Override
	public Element addLore(String loreLine)
	{
		getLore().add(loreLine);
		return this;
	}

	@Override
	public Element setBackground(boolean bg)
	{
		this.bg = bg;
		return this;
	}

	@Override
	public boolean isBackgrond()
	{
		return bg;
	}

	@Override
	public Element setCount(int c)
	{
		count = (int) M.clip(c, 1, 64);
		return this;
	}

	@Override
	public int getCount()
	{
		return count;
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack computeItemStack()
	{
		try
		{
			ItemStack is = new ItemStack(getMaterial().getMaterial(), getCount(), getEffectiveDurability(), getMaterial().getData());
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(getName());
			im.setLore(getLore().copy());

			if(isEnchanted())
			{
				im.addEnchant(Enchantment.DURABILITY, 1, true);
			}

			is.setItemMeta(im);
			return is;
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Element setProgress(double progress)
	{
		this.progress = M.clip(progress, 0D, 1D);
		return this;
	}

	@Override
	public double getProgress()
	{
		return progress;
	}

	@Override
	public short getEffectiveDurability()
	{
		if(getMaterial().getMaterial().getMaxDurability() == 0)
		{
			return 0;
		}

		else
		{
			int prog = (int) ((double) getMaterial().getMaterial().getMaxDurability() * (1D - getProgress()));
			return (short) M.clip(prog, 1, (getMaterial().getMaterial().getMaxDurability() - 1));
		}
	}
}
