package gerador;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        //EXEMPLO DE PREENCHIMENTO DOS VALORES NECESSARIOS PARA GERAR OS ARQUIVOS
        Mapeamento mapeamento = Mapeamento.builder()
                .adicionarPacoteDestino("br.com.exemplo.mapper")
                .adicionarNomeInterface("FilialMapper")
                .adicionarPacoteDominio("br.com.exemplo.model")
                .adicionarClasseDominio("Filial")
                .adicionarNomeInstanciaClasse("filial")
                .adicionarEsquema("cadastro")
                .adicionarTabela("ca04_entrada")
                .adicionarMapeamento("id", "ca04_id" )
                .adicionarMapeamento("nome", "ca04_nome" )
                .adicionarMapeamento("descricao", "ca04_descricao")
                .adicionarMapeamento("ativo", "ca04_ativo")
                .idIsUUID(false) //quando não é UUID então é Long
                .geraInterface(true)
                .geraXml(true)
                .diretorioDestino("/tmp/MAPPERS")
                .build();

        GeradorMapperMyBatis gerador = new GeradorMapperMyBatis();
        gerador.gerarArquivos(mapeamento);
    }
}
