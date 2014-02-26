package asciiWorld.tiles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class TileTransform {
	
	private float _rotation;
	private TransformEffect _effect;
	
	public TileTransform(float rotation, TransformEffect effect) {
		setRotation(rotation);
		setEffect(effect);
	}
	
	public TileTransform() {
		this(0.0f, TransformEffect.None);
	}
	
	protected TileTransform(TileTransform clone) {
		this(clone.getRotation(), clone.getEffect());
	}
	
	public float getRotation() {
		return _rotation;
	}
	
	public void setRotation(float value) {
		_rotation = value;
	}
	
	public TransformEffect getEffect() {
		return _effect;
	}
	
	public void setEffect(TransformEffect value) {
		_effect = value;
	}
	
	public static TileTransform load(String path) throws DataConversionException, JDOMException, IOException {
		return fromXml((Element)new SAXBuilder().build(new File(path)).getRootElement());
	}
	
	public static TileTransform fromXml(Element elem) throws DataConversionException {
		TileTransform transform = new TileTransform();
		
		Attribute rotationAttribute = elem.getAttribute("rotation");
		if (rotationAttribute != null) {
			transform.setRotation(rotationAttribute.getFloatValue());
		}
		
		Attribute effectAttribute = elem.getAttribute("effect");
		if (effectAttribute != null) {
			transform.setEffect(TransformEffect.valueOf(effectAttribute.getValue()));
		}
		
		return transform;
	}
	
	public void save(String path) throws Exception {
		Element elem = toXml();
		Document doc = new Document(elem);
		doc.setRootElement(elem);
		
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		xmlOutput.output(doc, new FileWriter(path));
		
		System.out.println(String.format("Saved Frame to '%s.'", path));
	}
	
	public Element toXml() throws Exception {
		Element transformElement = new Element("Transformation");
		transformElement.setAttribute("rotation", Float.toString(getRotation()));
		transformElement.setAttribute("effect", getEffect().name());
		return transformElement;
	}

	public TileTransform clone() {
		return new TileTransform(this);
	}
}
