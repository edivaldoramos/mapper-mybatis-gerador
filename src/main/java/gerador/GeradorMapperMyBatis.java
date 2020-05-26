package gerador;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * @author Edivaldo Ramos
 * @since 2020-05-10
 * Classe geradora de arquivos de interface java e xml com comandos basicos de CRUD
 * Arquivos com implementações de metodo usando o Framework MyBatis
 */
public class GeradorMapperMyBatis {

    private final String CABECALHO_PADRAO = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >";
    private final String PREFIXO_RESULT_MAP_NAME = "rm";

    public void gerarArquivos(Mapeamento mapeamento) throws IOException {
        if (mapeamento.geraXml()){
            this.gerarXmlMapper(mapeamento);
        }

        if (mapeamento.geraInterface()){
            this.gerarInterfaceMapper(mapeamento);
        }
    }

    private void gerarXmlMapper(Mapeamento mapeamento) throws IOException {
        if (Objects.isNull(mapeamento)) {
            return;
        }

        String nomeResultMap = String.format("%s%s", PREFIXO_RESULT_MAP_NAME, mapeamento.getClasseDominio());
        StringBuilder conteudo = new StringBuilder();
        conteudo.append(CABECALHO_PADRAO + "\n\n");
        conteudo.append(String.format("<mapper namespace=\"%s.%s\">\n\n", mapeamento.getPacoteMapper(), mapeamento.getNomeMapper()));
        conteudo.append(String.format("\t<resultMap id=\"%s\" type=\"%s.%s\">\n", nomeResultMap, mapeamento.getPacoteDominio(), mapeamento.getClasseDominio()));

        String colunaId = "";
        for (Map.Entry<String, String> e : mapeamento.getPropriedades().entrySet()) {
            if (e.getKey().equals("id")) {
                colunaId = e.getValue();
                conteudo.append(String.format("\t\t<id property=\"%s\" column=\"%s\"/>\n", e.getKey(), e.getValue()));
            }
        }

        StringBuilder colunasInsert = new StringBuilder();
        StringBuilder colunasSelect = new StringBuilder();
        StringBuilder colunasUpdate = new StringBuilder();
        StringBuilder propriedadesInsert = new StringBuilder();
        for (Map.Entry<String, String> e : mapeamento.getPropriedades().entrySet()) {
            if (!e.getKey().equals("id")) {
                conteudo.append(String.format("\t\t<result property=\"%s\" column=\"%s\"/>\n", e.getKey(), e.getValue()));
                colunasSelect.append("\t\t\t" + e.getValue().trim() + ",\n");
                colunasUpdate.append(String.format("\t\t%s = #{" + mapeamento.getNomeInstanciaDominio() + ".%s},\n", e.getValue(), e.getKey()));
                colunasInsert.append(e.getValue() + ",");
                propriedadesInsert.append(String.format("#{" + mapeamento.getNomeInstanciaDominio() + ".%s}, ", e.getKey()));
            }
        }
        conteudo.append("\t</resultMap>\n\n");

        //QUERY RECUPERAR TODOS
        conteudo.append(String.format("\t<select id=\"recuperarTodos\" resultMap=\"%s\">\n", nomeResultMap));
        conteudo.append("\t\tselect \n\t\t\t" + colunaId + ",\n" + reduzirString(colunasSelect.toString(), 2)
                + "\n\t\tfrom " + mapeamento.getEsquema() + "." + mapeamento.getTabela());
        conteudo.append("\n\t</select>");

        //QUERY RECUPERAR PELO ID
        conteudo.append(String.format("\n\n\t<select id=\"recuperar%sPorId\" resultMap=\"%s\">\n", mapeamento.getClasseDominio(), nomeResultMap));
        conteudo.append("\t\tselect \n\t\t\t" + colunaId + ",\n" + reduzirString(colunasSelect.toString(), 2)
                + "\n\t\tfrom " + mapeamento.getEsquema() + "." + mapeamento.getTabela()
                + "\n\t\twhere \n\t\t\t" + colunaId + " = #{" + mapeamento.getNomeInstanciaDominio() + ".id}");
        conteudo.append("\n\t</select>");

        //INSERT
        if (mapeamento.isIdUUID()) {
            conteudo.append("\n\n\t<insert id=\"inserir\">\n");
            conteudo.append("\t\tinsert " + mapeamento.getEsquema() + "." + mapeamento.getTabela());
            conteudo.append(" (" + colunaId + "," + reduzirString(colunasInsert.toString(), 1) + ")");
            conteudo.append("\n\t\tvalues (#{" + mapeamento.getNomeInstanciaDominio() + ".id}, " + reduzirString(propriedadesInsert.toString(),2) + ")");
        } else {
            conteudo.append(String.format("\n\n\t<insert id=\"inserir\" keyProperty=\"%s.id\" keyColumn=\"%s\" useGeneratedKeys=true>\n", mapeamento.getNomeInstanciaDominio(), colunaId));
            conteudo.append("\t\tinsert " + mapeamento.getEsquema() + "." + mapeamento.getTabela());
            conteudo.append(" (" + reduzirString(colunasInsert.toString(),1)+ ")");
            conteudo.append("\n\t\tvalues (" + reduzirString(propriedadesInsert.toString(),2)+ ")");
        }
        conteudo.append("\n\t</insert>");

        //UPDATE
        conteudo.append("\n\n\t<update id=\"atualizar\">\n");
        conteudo.append("\t\tupdate " + mapeamento.getEsquema() + "." + mapeamento.getTabela() + " set \n");
        conteudo.append(reduzirString(colunasUpdate.toString(),2));
        conteudo.append("\n\t\twhere \n\t\t\t" + colunaId + " = #{" + mapeamento.getNomeInstanciaDominio() + ".id}");
        conteudo.append("\n\t</update>");

        //DELETE
        conteudo.append("\n\n\t<delete id=\"excluir\">\n");
        conteudo.append("\t\tdelete " + mapeamento.getEsquema() + "." + mapeamento.getTabela());
        conteudo.append("\n\t\twhere \n\t\t\t" + colunaId + " = #{" + mapeamento.getNomeInstanciaDominio() + ".id}");
        conteudo.append("\n\t</delete>");

        conteudo.append("\n\n</mapper>");

        String nomeXmlMapper = mapeamento.getNomeMapper() + ".xml";
        escreverArquivo(conteudo.toString(), mapeamento.getDiretorioDestino(), nomeXmlMapper);
    }

    private void gerarInterfaceMapper(Mapeamento mapeamento) throws IOException {
        if (Objects.isNull(mapeamento)) {
            return;
        }

        StringBuilder conteudo = new StringBuilder();
        conteudo.append("package " + mapeamento.getPacoteMapper() + ";\n\n");
        conteudo.append("import org.apache.ibatis.annotations.Mapper;\n");
        conteudo.append("import org.apache.ibatis.annotations.Param;\n");
        conteudo.append("import org.springframework.stereotype.Repository;\n");
        if (mapeamento.getIsUUID()) {
            conteudo.append("import java.util.UUID;\n\n");
        } else {
            conteudo.append("import java.lang.Long;\n\n");
        }
        conteudo.append("@Mapper\n");
        conteudo.append("@Repository\n");
        conteudo.append("public interface " + mapeamento.getNomeMapper() + " {\n");

        String parametroID;
        if (mapeamento.getIsUUID()) {
            parametroID = "UUID id";
        } else {
            parametroID = "Long id";
        }

        conteudo.append(String.format("\tList<%s> recuperarTodos();\n\n", mapeamento.getClasseDominio()));
        conteudo.append(String.format("\t%s recuperar%sPorId(@Param(\"id\") %s);\n\n", mapeamento.getClasseDominio(), mapeamento.getClasseDominio(), parametroID));
        conteudo.append(String.format("\tvoid inserir(@Param(\"%s\") %s %s);\n\n", mapeamento.getNomeInstanciaDominio(), mapeamento.getClasseDominio(), mapeamento.getNomeInstanciaDominio()));
        conteudo.append(String.format("\tvoid atualizar(@Param(\"%s\") %s %s);\n\n", mapeamento.getNomeInstanciaDominio(), mapeamento.getClasseDominio(), mapeamento.getNomeInstanciaDominio()));
        conteudo.append(String.format("\tvoid excluir(@Param(\"%s\") %s %s);\n", mapeamento.getNomeInstanciaDominio(), mapeamento.getClasseDominio(), mapeamento.getNomeInstanciaDominio()));
        conteudo.append("}");

        String nomeInterfaceMapper = mapeamento.getNomeMapper() + ".java";
        escreverArquivo(conteudo.toString(), mapeamento.getDiretorioDestino(), nomeInterfaceMapper);
    }

    private void escreverArquivo(String conteudo, String diretorioDestino, String nomeArquivo) throws IOException {
        File diretorio = new File(diretorioDestino);
        if (!diretorio.exists() && !diretorio.mkdir()) {
            return;
        }

        File arquivo = new File(diretorio.getAbsolutePath() + "/" + nomeArquivo);

        if (arquivo.exists() && !arquivo.delete()) {
            return;
        }

        if (!arquivo.exists() && arquivo.createNewFile()) {
            FileWriter fw = new FileWriter(arquivo.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(conteudo);
            bw.close();
        }

        System.out.println("Arquivo foi gerado na pasta " + diretorioDestino);
    }

    private String reduzirString(String conteudo, int tamanhoReducao){
        return conteudo.substring(0, conteudo.length() - tamanhoReducao);
    }
}
