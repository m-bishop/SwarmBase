package psu.edu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

@Entity(name="PS_PERS_NID")
@XmlRootElement
public class NID implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Id
    @Column(name = "EmplID")
    private String emplid;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "NATIONAL_ID_TYPE")
    private String nationalIdType;

    @Column(name = "NATIONAL_ID")
    private String nationalId;

    @Column(name = "SSN_KEY_FRA")
    private String ssnKeyFra;

    @Column(name = "PRIMARY_NID")
    private String primaryNid;

    @Column(name = "TAX_REF_ID_SGP")
    private String taxRefIdSgp;

    @Column(name = "LASTUPDDTTM", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String lastUpdDtTm;

    @Column(name = "LASTUPDOPRID")
    private String lastUpdOprid;

    public String getEmplid() {
        return emplid;
    }

    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getNationalIdType() {
        return nationalIdType;
    }

    public void setNationalIdType(String nationalIdType) {
        this.nationalIdType = nationalIdType;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getSsnKeyFra() {
        return ssnKeyFra;
    }

    public void setSsnKeyFra(String ssnKeyFra) {
        this.ssnKeyFra = ssnKeyFra;
    }

    public String getPrimaryNid() {
        return primaryNid;
    }

    public void setPrimaryNid(String primaryNid) {
        this.primaryNid = primaryNid;
    }

    public String getTaxRefIdSgp() {
        return taxRefIdSgp;
    }

    public void setTaxRefIdSgp(String taxRefIdSgp) {
        this.taxRefIdSgp = taxRefIdSgp;
    }

    public String getLastUpdDtTm() {
        return lastUpdDtTm;
    }

    public void setLastUpdDtTm(String lastUpdDtTm) {
        this.lastUpdDtTm = lastUpdDtTm;
    }

    public String getLastUpdOprid() {
        return lastUpdOprid;
    }

    public void setLastUpdOprid(String lastUpdOprid) {
        this.lastUpdOprid = lastUpdOprid;
    }
}