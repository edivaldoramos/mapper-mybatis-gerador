package gerador;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Edivaldo Ramos
 * @since 2020-05-10
 * Classe de mapeamento de valores necessarios para a
 * geração dos arquivos de interface e xml de mappers do mybatis
 */
@Data
public class Mapeamento {
    private String esquema;
    private String tabela;
    private String pacoteMapper;
    private String nomeMapper;
    private String pacoteDominio;
    private String classeDominio;
    private String nomeInstanciaDominio;
    private String diretorioDestino;
    private Map<String, String> propriedades;
    private Boolean isUUID;
    private Boolean geraXml;
    private Boolean geraInterface;

    public Mapeamento() {
    }

    public Mapeamento(MapeamentoBuilder builder){
        this.esquema = builder.esquema;
        this.tabela = builder.tabela;
        this.pacoteMapper = builder.pacoteMapper;
        this.nomeMapper = builder.nomeMapper;
        this.classeDominio = builder.classeDominio;
        this.pacoteDominio = builder.pacoteDominio;
        this.nomeInstanciaDominio = builder.nomeInstanciaDominio;
        this.propriedades = builder.mapeamento;
        this.isUUID = builder.isUUID;
        this.geraInterface = builder.geraInterface;
        this.geraXml = builder.geraXml;
        this.diretorioDestino = builder.diretorioDestino;
    }

    public Boolean isIdUUID(){
        return isUUID;
    }

    public Boolean geraXml(){
        return geraXml;
    }

    public Boolean geraInterface(){
        return getGeraInterface();
    }

    public static Mapeamento.MapeamentoBuilder builder(){
        return new MapeamentoBuilder();
    }

    public static class MapeamentoBuilder {
        private final String DIRETORIO_PADRAO_LINUX = "/tmp/MAPPERS";

        private String esquema;
        private String tabela;
        private String pacoteMapper;
        private String nomeMapper;
        private String pacoteDominio;
        private String classeDominio;
        private String nomeInstanciaDominio;
        private String diretorioDestino;
        private Map<String, String> mapeamento;
        private Boolean isUUID;
        private Boolean geraXml;
        private Boolean geraInterface;

        private MapeamentoBuilder(){
            this.mapeamento = new HashMap<>();
            this.isUUID = false;
            this.geraXml = Boolean.TRUE;
            this.geraInterface = Boolean.TRUE;
            this.diretorioDestino = DIRETORIO_PADRAO_LINUX;
        }

        public MapeamentoBuilder adicionarMapeamento(String propriedade, String coluna){
            mapeamento.put(propriedade, coluna);
            return this;
        }

        public MapeamentoBuilder adicionarInstanciaClasse(String nomeInstanciaClasse){
            this.nomeInstanciaDominio = nomeInstanciaClasse;
            return this;
        }

        public MapeamentoBuilder adicionarPacoteDominio(String pacoteDominio){
            this.pacoteDominio = pacoteDominio;
            return this;
        }

        public MapeamentoBuilder adicionarTabela(String tabela){
            this.tabela = tabela;
            return this;
        }

        public MapeamentoBuilder adicionarEsquema(String esquema){
            this.esquema = esquema;
            return this;
        }

        public MapeamentoBuilder geraXml(Boolean geraXml){
            this.geraXml = geraXml;
            return this;
        }

        public MapeamentoBuilder geraInterface(Boolean geraInterface){
            this.geraInterface = geraInterface;
            return this;
        }

        public MapeamentoBuilder adicionarNomeMapper(String nomeMapper){
            this.nomeMapper = nomeMapper;
            return this;
        }

        public MapeamentoBuilder adicionarPacoteDestino(String pacoteDestino){
            this.pacoteMapper = pacoteDestino;
            return this;
        }

        public MapeamentoBuilder idIsUUID(Boolean isUUID){
            this.isUUID = isUUID;
            return this;
        }

        public MapeamentoBuilder adicionarClasseDominio(String classeDominio){
            this.classeDominio = classeDominio;
            return this;
        }

        public MapeamentoBuilder diretorioDestino(String diretorioDestino) {
            this.diretorioDestino = diretorioDestino;
            return this;
        }

        public Mapeamento build(){
            return new Mapeamento(this);
        }
    }
}
