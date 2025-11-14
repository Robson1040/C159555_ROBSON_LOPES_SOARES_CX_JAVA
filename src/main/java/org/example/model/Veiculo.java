package org.example.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.example.dto.VeiculoRequest;

import java.util.List;

@Entity
public class Veiculo extends PanacheEntityBase
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    public Long id;

    public VeiculoStatus status;
    public String brand;
    public String model;

    @Column(name = "vehicle_year")
    public int year;
    public String engine;

    protected Veiculo()
    {

    }

    public Veiculo(String brand, String model, int year, String engine)
    {
        if(brand == null || model == null || engine == null || year <= 0)
        {
            throw new IllegalArgumentException("Nenhum campo deve ser null");
        }

        if(brand.trim().equals("") || model.trim().equals("") || engine.trim().equals("") || year <= 0)
        {
            throw new IllegalArgumentException("Nenhum campo deve ser null");
        }

        this.status = VeiculoStatus.AVAILABLE;
        this.brand  = brand;
        this.model  = model;
        this.year   = year;
        this.engine = engine;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VeiculoStatus getStatus() {
        return status;
    }


    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public static List<Veiculo> buscarPorCarTitle(String filtro) {
        return find("concat(nome, ' ', sobrenome, ' ', cargo) = ?1", filtro).list();
    }

    public String getCarTitle()
    {
        return this.getBrand() +
                " " +
                this.getModel() +
                " " +
                this.getYear();
    }

    public void atualizaStatus(VeiculoStatus status)
    {
        if(status.equals(VeiculoStatus.RENTED) && !this.getStatus().equals(VeiculoStatus.AVAILABLE))
        {
            throw new IllegalArgumentException("Um veículo só pode ser alterado para RENTED se seu status atual for AVAILABLE");
        }

        if(status.equals(VeiculoStatus.AVAILABLE) && !(this.getStatus().equals(VeiculoStatus.RENTED) || this.getStatus().equals(VeiculoStatus.UNDER_MAINTENANCE)))
        {
            throw new IllegalArgumentException("Um veículo só pode ser alterado para AVAILABLE se seu status atual for RENTED ou UNDER_MAINTENANCE.");
        }

        //Pode ir para UNDER_MAINTENANCE de qualquer status.
        //if(s.equals(VeiculoStatus.UNDER_MAINTENANCE))
        //{
        //  return true;
        // }

        this.status = status;
    }
}