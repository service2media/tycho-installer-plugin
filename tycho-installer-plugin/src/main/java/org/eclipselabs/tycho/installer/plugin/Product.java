package org.eclipselabs.tycho.installer.plugin;

import java.io.File;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import com.google.common.annotations.VisibleForTesting;

public class Product {
	public final String id;
    public final String name;
    public final String version;
    public final String manufacturer;
    public final String launcherName;
    public final String upgradeCode;
    public final String licenseText;

    @VisibleForTesting
    public Product(String name, String version, String manufacturer, String licenseText, String launcher, String upgradeCode) {
    	this.id = name;
        this.name = name;
        this.version = version.replace(".qualifier", "");
        this.manufacturer = manufacturer;
        this.upgradeCode = upgradeCode;
        this.launcherName = launcher;
        this.licenseText = licenseText;
    }
    
    public Product(File productFile, String manufacturer, String buildQualifier) throws Exception {
    	DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    	Document productConfig = documentBuilder.parse(productFile);
    	this.id = valueFromXPath(productConfig, "/product/@uid");
		this.name = valueFromXPath(productConfig, "/product/@name");
		this.version = replaceQualifier(valueFromXPath(productConfig, "/product/@version"), buildQualifier);
		this.launcherName = valueFromXPath(productConfig, "/product/launcher/@name");
		this.manufacturer = manufacturer;
		this.upgradeCode = UUID.nameUUIDFromBytes(name.getBytes()).toString();
		this.licenseText = valueFromXPath(productConfig, "/product/license/text");
	}

	private String replaceQualifier(String osgiVersion, String buildQualifier) {
		return buildQualifier != null ? osgiVersion.replace(".qualifier", "." + buildQualifier) : osgiVersion;
	}

	private String valueFromXPath(Document productConfig, String xpath) throws XPathExpressionException {
		return javax.xml.xpath.XPathFactory.newInstance().newXPath().evaluate(xpath, productConfig);
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", version=" + version
				+ ", manufacturer=" + manufacturer + ", launcherName="
				+ launcherName + ", upgradeCode=" + upgradeCode
				+ ", licenseText=" + licenseText + "]";
	}
}
