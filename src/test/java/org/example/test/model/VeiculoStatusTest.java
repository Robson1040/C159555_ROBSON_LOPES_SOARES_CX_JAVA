package org.example.test.model;

import org.example.model.VeiculoStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class VeiculoStatusTest 
{
    @Test
    @DisplayName("Verificar valores do enum VeiculoStatus")
    public void testEnumValues()
    {
        VeiculoStatus[] values = VeiculoStatus.values();
        
        Assertions.assertEquals(3, values.length);
        Assertions.assertEquals(VeiculoStatus.AVAILABLE, values[0]);
        Assertions.assertEquals(VeiculoStatus.RENTED, values[1]);
        Assertions.assertEquals(VeiculoStatus.UNDER_MAINTENANCE, values[2]);
    }

    @Test
    @DisplayName("Verificar valueOf do enum")
    public void testValueOf()
    {
        Assertions.assertEquals(VeiculoStatus.AVAILABLE, VeiculoStatus.valueOf("AVAILABLE"));
        Assertions.assertEquals(VeiculoStatus.RENTED, VeiculoStatus.valueOf("RENTED"));
        Assertions.assertEquals(VeiculoStatus.UNDER_MAINTENANCE, VeiculoStatus.valueOf("UNDER_MAINTENANCE"));
    }

    @Test
    @DisplayName("Verificar toString do enum")
    public void testToString()
    {
        Assertions.assertEquals("AVAILABLE", VeiculoStatus.AVAILABLE.toString());
        Assertions.assertEquals("RENTED", VeiculoStatus.RENTED.toString());
        Assertions.assertEquals("UNDER_MAINTENANCE", VeiculoStatus.UNDER_MAINTENANCE.toString());
    }

    @Test
    @DisplayName("Verificar equals do enum")
    public void testEquals()
    {
        Assertions.assertEquals(VeiculoStatus.AVAILABLE, VeiculoStatus.AVAILABLE);
        Assertions.assertEquals(VeiculoStatus.RENTED, VeiculoStatus.RENTED);
        Assertions.assertEquals(VeiculoStatus.UNDER_MAINTENANCE, VeiculoStatus.UNDER_MAINTENANCE);
        
        Assertions.assertNotEquals(VeiculoStatus.AVAILABLE, VeiculoStatus.RENTED);
        Assertions.assertNotEquals(VeiculoStatus.RENTED, VeiculoStatus.UNDER_MAINTENANCE);
        Assertions.assertNotEquals(VeiculoStatus.AVAILABLE, VeiculoStatus.UNDER_MAINTENANCE);
    }
}