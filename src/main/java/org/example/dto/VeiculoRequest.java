package org.example.dto;

import jakarta.persistence.Column;
import org.example.model.VeiculoStatus;

public record VeiculoRequest
        (
            String brand,
            String model,
            int year,
            String engine
        )
{

}
