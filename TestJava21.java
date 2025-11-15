public class TestJava21 {
    
    // Usando record (Java 14+, estável no Java 16+)
    public record Person(String name, int age, String role) {}
    
    // Usando pattern matching para switch (Java 21)
    public static String processValue(Object value) {
        return switch (value) {
            case String s when s.length() > 10 -> "Long string: " + s;
            case String s -> "Short string: " + s;
            case Integer i when i > 100 -> "Big number: " + i;
            case Integer i -> "Small number: " + i;
            case Person(var name, var age, var role) -> 
                "Person: " + name + " (" + age + " years, " + role + ")";
            case null -> "Null value";
            default -> "Unknown type: " + value.getClass().getSimpleName();
        };
    }
    
    public static void main(String[] args) {
        System.out.println("=== TESTE JAVA 21 ===");
        
        // Testando records
        var person = new Person("João Silva", 30, "ADMIN");
        System.out.println("Record created: " + person);
        System.out.println("Name: " + person.name());
        System.out.println("Age: " + person.age());
        System.out.println("Role: " + person.role());
        
        // Testando pattern matching
        System.out.println("\n=== Pattern Matching Tests ===");
        System.out.println(processValue("Hello"));
        System.out.println(processValue("This is a very long string"));
        System.out.println(processValue(50));
        System.out.println(processValue(150));
        System.out.println(processValue(person));
        System.out.println(processValue(null));
        System.out.println(processValue(3.14));
        
        System.out.println("\n✅ Java 21 está funcionando perfeitamente!");
        System.out.println("✅ Records funcionais");  
        System.out.println("✅ Pattern matching funcional");
        System.out.println("✅ Ambiente configurado com sucesso!");
    }
}