package br.com.caelum.pm73.dao;

import br.com.caelum.pm73.dominio.Usuario;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UsuarioDaoTest {

    Session session;
    UsuarioDao usuarioDao;

    @Before
    public void setup() {
        session = new CriadorDeSessao().getSession();
        usuarioDao = new UsuarioDao(session);

        session.beginTransaction();
    }

    @Test
    public void deveEncontrarPeloNomeEEmailMockado() {
        Usuario usuario = new Usuario("Joao da Silva", "joao@dasilva.com.br");
        usuarioDao.salvar(usuario);

        Usuario usuarioBanco = usuarioDao.porNomeEEmail("Joao da Silva", "joao@dasilva.com.br");

        assertEquals("Joao da Silva", usuarioBanco.getNome());
        assertEquals("joao@dasilva.com.br", usuarioBanco.getEmail());
    }

    @Test
    public void deveRetornarNullQuandoUsuarioNaoExistir() {
        Usuario usuarioBanco = usuarioDao.porNomeEEmail("Joao da Silva", "joao@dasilva.com.br");

        assertNull(usuarioBanco);
    }

    @After
    public void finish() {
        session.getTransaction().rollback();
        session.close();
    }

    @Test
    public void deveDeletarUsuario() {
        Usuario usuario = new Usuario("Joao da Silva", "joao@dasilva.com.br");

        usuarioDao.salvar(usuario);
        usuarioDao.deletar(usuario);

        session.flush();
        session.clear();

        Usuario usuarioBanco = usuarioDao.porNomeEEmail("Joao da Silva", "joao@dasilva.com.br");

        assertNull(usuarioBanco);
    }

    /**
     * Crie um usuário e salve-o no banco. Em seguida, altere o nome e e-mail
     * e faça uma alteração. Faça um flush para garantir que os comandos chegaram no banco.
     * Em seguida, faça duas buscas: uma buscando o nome antigo e outra buscando o nome novo.
     */
    @Test
    public void deveAlterarUsuario() {
        Usuario usuario = new Usuario("Joao da Silva", "joao@dasilva.com.br");

        usuarioDao.salvar(usuario);

        usuario.setEmail("update@mail.com");
        usuarioDao.atualizar(usuario);

        session.flush();

        assertNull(usuarioDao.porNomeEEmail("Joao da Silva", "joao@dasilva.com.br"));
        assertNotNull(usuarioDao.porNomeEEmail("Joao da Silva", "update@mail.com"));
    }

    /*@Test
    public void deveEncontrarPeloNomeEEmailMockado() {
        Session session = Mockito.mock(Session.class);
        Query query = Mockito.mock(Query.class);

        UsuarioDao usuarioDao = new UsuarioDao(session);

        Usuario usuario = new Usuario("Joao da Silva", "joao@dasilva.com.br");

        String sql = "from Usuario u where u.nome = :nome and u.email = :email";
        Mockito.when(session.createQuery(sql)).thenReturn(query);
        Mockito.when(query.uniqueResult()).thenReturn(usuario);
        Mockito.when(query.setParameter("nome", usuario.getNome())).thenReturn(query);
        Mockito.when(query.setParameter("email", usuario.getEmail())).thenReturn(query);

        Usuario result = usuarioDao.porNomeEEmail(usuario.getNome(), usuario.getEmail());

        assertEquals(usuario, result);
    }*/
}