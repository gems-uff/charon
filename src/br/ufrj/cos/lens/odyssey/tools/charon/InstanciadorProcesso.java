package br.ufrj.cos.lens.odyssey.tools.charon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Classe respossável por instanciar um processo
 *
 * @author Leonardo Murta
 * @version 1.0, 22/11/2001
 */
public class InstanciadorProcesso {

	/**
	 * Vetor de paineis do wizard
	 */
	Vector paineis = null;

	/**
	 * Informa se o processo foi instanciado corretamente
	 */
	private boolean instanciado = false;

	/**
	 * Base de conhecimento do processo
	 */
	private BaseConhecimento base = null;

	/**
	 * Lista de papeis existentes
	 */
	private List papeis = null;

	/**
	 * Constroi o instanciador
	 */
	public InstanciadorProcesso(BaseConhecimento base) {
		// (0) Utiliza a base existente (se não for null)
		this.base = base;

//		// (1) Cria e prepara os paineis
//		paineis = new Vector();
//
//		// (1.1) Painel de seleçao do processo raiz
//		if (base == null) {
//			Vector processos = Ambiente.getInstancia().getInfraEstrutura().pegaListaItens(Categoria.idProcesso);
//			painelSelecaoProcessoRaiz = new PainelSelecaoProcessoRaiz(processos);
//			paineis.add(painelSelecaoProcessoRaiz);
//		}
//
//		// (1.2) Painel de associaçao de papeis com usuários
//		Vector usuarios = Ambiente.getInstancia().getInfraEstrutura().pegaListaItens(Categoria.idUsuario);
//		painelAsociacaoPapeisUsuarios = new PainelAsociacaoPapeisUsuarios(usuarios);
//		paineis.add(painelAsociacaoPapeisUsuarios);

//		if (base != null) {
//			base.atualizaBase();
//			papeis = base.getPapeis();
//			painelAsociacaoPapeisUsuarios.setPapeis(papeis);
//		}
	}

	/**
	 * Informa se o processo de instanciacao ocorreu dentro do esperado
	 */
	public boolean isInstanciado() {
		return instanciado;
	}

	/**
	 * Fornece a base de conhecimento criada
	 *
	 * @return Base de conhecimento que foi criada
	 */
	public BaseConhecimento getBaseConhecimento() {
		return base;
	}

	/** avançar o painel do wizard */
	public void avancar() {
//				NoProcesso processo = painelSelecaoProcessoRaiz.getProcessoSelecionado();
//				base = new BaseConhecimento(processo);
//				papeis = base.getPapeis();
//				painelAsociacaoPapeisUsuarios.setPapeis(papeis);
	}

	/** finaliza o wizard */
	public boolean finalizar() {
//			List prolog = new ArrayList();
//			Iterator iteratorPapeis = papeis.iterator();
//			while (iteratorPapeis.hasNext()) {
//				Papel papel = (Papel)iteratorPapeis.next();
//
//				List fatos = papel.getProlog();
//
//				// Verifica se todos os papeis foram atribuídos a ao menos um usuário
//				if (fatos.size() == 0) {
//					JOptionPane.showMessageDialog(wizard, "You must assign at least one user to each role!", "Process Wizard", JOptionPane.WARNING_MESSAGE);
//					return false;
//				} else {
//					prolog.addAll(fatos);
//				}
//			}
//			base.getProlog().addClausulas(prolog.iterator());
//			instanciado = true;
//		}

		return true;
	}
}