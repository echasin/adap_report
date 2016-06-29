package com.innvo.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.elasticsearch.annotations.Document;

import com.innvo.json.StringJsonUserType;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Reportparameter.
 */
@Entity
@Table(name = "reportparameter")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "reportparameter")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = StringJsonUserType.class)})
public class Reportparameter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "label", length = 100, nullable = false)
    private String label;

    @Size(max = 100)
    @Column(name = "instructions", length = 100)
    private String instructions;

    @NotNull
    @Size(max = 25)
    @Column(name = "datatype", length = 25, nullable = false)
    private String datatype;
    
    @NotNull
    @Column(name = "required", nullable = false)
    private String required;
    
    @NotNull
    @Column(name = "minlength", nullable = false)
    private String minlength;
    
    @NotNull
    @Size(max = 25)
    @Column(name = "maxlength", nullable = false)
    private String maxlength;
    
    
    @Column(name = "validation")
    @Type(type = "StringJsonObject")
    private String validation;

    @NotNull
    @Size(max = 25)
    @Column(name = "status", length = 25, nullable = false)
    private String status;

    @NotNull
    @Size(max = 50)
    @Column(name = "lastmodifiedby", length = 50, nullable = false)
    private String lastmodifiedby;

    @NotNull
    @Column(name = "lastmodifieddatetime", nullable = false)
    private ZonedDateTime lastmodifieddatetime;

    @NotNull
    @Size(max = 25)
    @Column(name = "domain", length = 25, nullable = false)
    private String domain;

    @ManyToOne
    @NotNull
    private Report report;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

	public String getValidation() {
		return validation;
	}

	public void setValidation(String validation) {
		this.validation = validation;
	}

	
	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	public String getMinlength() {
		return minlength;
	}

	public void setMinlength(String minlength) {
		this.minlength = minlength;
	}

	public String getMaxlength() {
		return maxlength;
	}

	public void setMaxlength(String maxlength) {
		this.maxlength = maxlength;
	}

	public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastmodifiedby() {
        return lastmodifiedby;
    }

    public void setLastmodifiedby(String lastmodifiedby) {
        this.lastmodifiedby = lastmodifiedby;
    }

    public ZonedDateTime getLastmodifieddatetime() {
        return lastmodifieddatetime;
    }

    public void setLastmodifieddatetime(ZonedDateTime lastmodifieddatetime) {
        this.lastmodifieddatetime = lastmodifieddatetime;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Reportparameter reportparameter = (Reportparameter) o;
        if(reportparameter.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, reportparameter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Reportparameter{" +
            "id=" + id +
            ", label='" + label + "'" +
            ", instructions='" + instructions + "'" +
            ", datatype='" + datatype + "'" +
            ", status='" + status + "'" +
            ", lastmodifiedby='" + lastmodifiedby + "'" +
            ", lastmodifieddatetime='" + lastmodifieddatetime + "'" +
            ", domain='" + domain + "'" +
            '}';
    }
}
