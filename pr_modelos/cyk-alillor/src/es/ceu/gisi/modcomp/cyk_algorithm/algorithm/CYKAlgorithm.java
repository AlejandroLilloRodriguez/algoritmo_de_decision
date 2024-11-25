package es.ceu.gisi.modcomp.cyk_algorithm.algorithm;

import es.ceu.gisi.modcomp.cyk_algorithm.algorithm.exceptions.CYKAlgorithmException;
import es.ceu.gisi.modcomp.cyk_algorithm.algorithm.interfaces.CYKAlgorithmInterface;
import java.util.*;
/**
 * Esta clase contiene la implementación de la interfaz CYKAlgorithmInterface
 * que establece los métodos necesarios para el correcto funcionamiento del
 * proyecto de programación de la asignatura Modelos de Computación.
 *
 * @author Sergio Saugar García <sergio.saugargarcia@ceu.es>
 */
public class CYKAlgorithm implements CYKAlgorithmInterface {

    private Map<Character, Set<String>> produccion = new HashMap<>();
    private Set<Character> noTerminales = new HashSet<>();
    private Set<Character> terminales = new HashSet<>();
    private ArrayList<String>[][] tabla;
    private char axioma;
    
    @Override
    /**
     * Método que añade los elementos no terminales de la gramática.
     *
     * @param nonterminal Por ejemplo, 'S'
     * @throws CYKAlgorithmException Si el elemento no es una letra mayúscula.
     */
    public void addNonTerminal(char nonterminal) throws CYKAlgorithmException {
        
        if (Character.isUpperCase(nonterminal)&& !noTerminales.contains(nonterminal)) {
            noTerminales.add(nonterminal);
        } else {
            throw new CYKAlgorithmException();
        }
    }

    @Override
    /**
     * Método que añade los elementos terminales de la gramática.
     *
     * @param terminal Por ejemplo, 'a'
     * @throws CYKAlgorithmException Si el elemento no es una letra minúscula.
     */
    public void addTerminal(char terminal) throws CYKAlgorithmException {
        if (Character.isLowerCase(terminal)&& !terminales.contains(terminal)) {
             terminales.add(terminal);
        }else{
       throw new CYKAlgorithmException();
    }
    }

    @Override
    /**
     * Método que indica, de los elementos no terminales, cuál es el axioma de
     * la gramática.
     *
     * @param nonterminal Por ejemplo, 'S'
     * @throws CYKAlgorithmException Si el elemento insertado no forma parte del
     * conjunto de elementos no terminales.
     */
    public void setStartSymbol(char nonterminal) throws CYKAlgorithmException {
      if (!noTerminales.contains(nonterminal)) {
            throw new CYKAlgorithmException();
        } else {
            axioma = nonterminal;
        }
    }

    @Override
    /**
     * Método utilizado para construir la gramática. Admite producciones en FNC,
     * es decir de tipo A::=BC o A::=a
     *
     * @param nonterminal A
     * @param production "BC" o "a"
     * @throws CYKAlgorithmException Si la producción no se ajusta a FNC o está
     * compuesta por elementos (terminales o no terminales) no definidos
     * previamente.
     */
    public void addProduction(char nonterminal, String production) throws CYKAlgorithmException {
    if (!noTerminales.contains(nonterminal)) {
        throw new CYKAlgorithmException();
    }

    if ((production.length() == 2 && Character.isUpperCase(production.charAt(0)) && Character.isUpperCase(production.charAt(1))
            && noTerminales.contains(production.charAt(0)) && noTerminales.contains(production.charAt(1)) && nonterminal != production.charAt(0))
        || (production.length() == 1 && Character.isLowerCase(production.charAt(0)) && terminales.contains(production.charAt(0)))) {

        Set<String> productions = produccion.getOrDefault(nonterminal, new HashSet<>());
        if (productions.contains(production)) {
            throw new CYKAlgorithmException();
        }
        productions.add(production);
        produccion.put(nonterminal, productions);
    } else {
        throw new CYKAlgorithmException();
    }
}
    // necesito crear un metodo para obtener los no terminales de un terminal , para poder meterlos en las celdas de la tabla
    public Set<Character> ObtenerTerminalesCelda(char terminal) {
    Set<Character> listaSet = new HashSet<>();
    for (Map.Entry<Character, Set<String>> entry : produccion.entrySet()) {
        char nonterminal = entry.getKey();
        Set<String> productions = entry.getValue();
        for (String production : productions) {
            if (production.length() == 1 && production.charAt(0) == terminal) {
                listaSet.add(nonterminal);
            }
        }
    }
    
    return listaSet;
    }
    @Override
    /**
     * Método que indica si una palabra pertenece al lenguaje generado por la
     * gramática que se ha introducido.
     *
     * @param word La palabra a verificar, tiene que estar formada sólo por
     * elementos no terminales.
     * @return TRUE si la palabra pertenece, FALSE en caso contrario
     * @throws CYKAlgorithmException Si la palabra proporcionada no está formada
     * sólo por terminales, si está formada por terminales que no pertenecen al
     * conjunto de terminales definido para la gramática introducida, si la
     * gramática es vacía o si el autómata carece de axioma.
     */
    public boolean isDerived(String word) throws CYKAlgorithmException {
    int n = word.length();
    if (n == 0 || noTerminales.isEmpty() || axioma == '\u0000') {
        throw new CYKAlgorithmException();
    }

    Set<Character>[][] table = new HashSet[n][n];

    for (int i = 0; i < n; i++) {
        char terminal = word.charAt(i);
        if (!terminales.contains(terminal)) {
            throw new CYKAlgorithmException();
        }
        table[i][i] = ObtenerTerminalesCelda(terminal);
    }

    for (int l = 2; l <= n; l++) {
        for (int i = 0; i <= n - l; i++) {
            int j = i + l - 1;
            table[i][j] = new HashSet<>();
            for (int k = i; k < j; k++) {
                Set<Character> noterminales1 = table[i][k];
                Set<Character> noterminales2 = table[k + 1][j];
                for (char nonterminal : noTerminales) {
                    for (String production : produccion.getOrDefault(nonterminal, Collections.emptySet())) {
                        if (production.length() == 2) {
                            char symbolA = production.charAt(0);
                            char symbolB = production.charAt(1);
                            if (noterminales1.contains(symbolA) && noterminales2.contains(symbolB)) {
                                table[i][j].add(nonterminal);
                            }
                        }
                    }
                }
            }
        }
    }

    return table[0][n - 1].contains(axioma);
}
    

    @Override
    /**
     * Método que, para una palabra, devuelve un String que contiene todas las
     * celdas calculadas por el algoritmo (la visualización debe ser similar al
     * ejemplo proporcionado en el PDF que contiene el paso a paso del
     * algoritmo).
     *
     * @param word La palabra a verificar, tiene que estar formada sólo por
     * elementos no terminales.
     * @return Un String donde se vea la tabla calculada de manera completa,
     * todas las celdas que ha calculado el algoritmo.
     * @throws CYKAlgorithmException Si la palabra proporcionada no está formada
     * sólo por terminales, si está formada por terminales que no pertenecen al
     * conjunto de terminales definido para la gramática introducida, si la
     * gramática es vacía o si el autómata carece de axioma.
     */
    public String algorithmStateToString(String word) throws CYKAlgorithmException {
        if (word.isEmpty()) {
            throw new UnsupportedOperationException("Not supported yet.");        }
        if (noTerminales.isEmpty()) {
            throw new UnsupportedOperationException("Not supported yet.");        }
        if (axioma == 0) {
            throw new UnsupportedOperationException("Not supported yet.");        }
        for (int i = 0; i < word.length(); i++) {
            if (!terminales.contains(word.charAt(i))) {
                throw new UnsupportedOperationException("Not supported yet.");            }
        }
        for (int i = 0; i < word.length(); i++) {
            if (!Character.isLowerCase(word.charAt(i))) {
                throw new UnsupportedOperationException("Not supported yet.");            }
        }
        if (isDerived(word)==true) {
            String tabla2 = "";
            for (int i = 0; i < word.length(); i++) {
                for (int j = 0; j < word.length(); j++) {
                    tabla2 += tabla[i][j].toString() + " ";
                }
                tabla2 += "\n";
            }
            return tabla2;
        } else {
            return "La palabra no pertenece al lenguaje de la gramatica";
        }
    }

    @Override
    /**
     * Elimina todos los elementos que se han introducido hasta el momento en la
     * gramática (elementos terminales, no terminales, axioma y producciones),
     * dejando el algoritmo listo para volver a insertar una gramática nueva.
     */
    public void removeGrammar() {
        if (noTerminales != null) {
            noTerminales.clear();
        }
        if (terminales != null) {
            terminales.clear();
        }
        
        if (produccion != null) {
            produccion.clear();
        }
        axioma = '\u0000';
    }

    @Override
    /**
     * Devuelve un String que representa todas las producciones que han sido
     * agregadas a un elemento no terminal.
     *
     * @param nonterminal
     * @return Devuelve un String donde se indica, el elemento no terminal, el
     * símbolo de producción "::=" y las producciones agregadas separadas, única
     * y exclusivamente por una barra '|' (no incluya ningún espacio). Por
     * ejemplo, si se piden las producciones del elemento 'S', el String de
     * salida podría ser: "S::=AB|BC".
     */
    public String getProductions(char nonterminal) {
        StringBuilder productionsString = new StringBuilder();
    Set<String> productions = produccion.getOrDefault(nonterminal, Collections.emptySet());
    
    if (!productions.isEmpty()) {
        productionsString.append(nonterminal).append("::=");
        List<String> produccionOrdenada = new ArrayList<>(productions);
        Collections.sort(produccionOrdenada);
        
        for (int i = 0; i < produccionOrdenada.size(); i++) {
            if (i > 0) {
                productionsString.append("|");
            }
            productionsString.append(produccionOrdenada.get(i));
        }
    }
    
    return productionsString.toString();
}
    @Override
    /**
     * Devuelve un String con la gramática completa.
     *
     * @return Devuelve el agregado de hacer getProductions sobre todos los
     * elementos no terminales.
     */
    public String getGrammar() {
      StringBuilder grammar = new StringBuilder();

 

    for (char nonTerminal : noTerminales) {

        if (produccion.containsKey(nonTerminal)) {

            grammar.append(nonTerminal).append(" := ").append(produccion.get(nonTerminal)).append("\n");

        }
    }
    return grammar.toString();
    }

}
// Practica terminada 
