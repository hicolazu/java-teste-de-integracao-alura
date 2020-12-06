package br.com.caelum.pm73.dao;

import br.com.caelum.pm73.dominio.Leilao;
import br.com.caelum.pm73.dominio.Usuario;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;

public class LeilaoDaoTest {

    Session session;
    UsuarioDao usuarioDao;
    LeilaoDao leilaoDao;

    @Before
    public void setup() {
        session = new CriadorDeSessao().getSession();
        usuarioDao = new UsuarioDao(session);
        leilaoDao = new LeilaoDao(session);

        session.beginTransaction();
    }

    @Test
    public void deveContarLeiloesNaoEncerrados() {
        Usuario mauricio = new Usuario("Mauricio", "mauricio@hotmail.com");

        Leilao ativo = new Leilao("Geladeira", 1500.00, mauricio, false);
        Leilao encerrado = new Leilao("XBox", 700.00, mauricio, false);
        encerrado.encerra();

        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(ativo);
        leilaoDao.salvar(encerrado);

        long total = leilaoDao.total();

        assertEquals(1L, total);
    }

    @Test
    public void deveRetornar0CasoNaoHajaLeilaoAtivo() {
        Usuario mauricio = new Usuario("Mauricio", "mauricio@hotmail.com");

        Leilao encerrado1 = new Leilao("Geladeira", 1500.00, mauricio, false);
        Leilao encerrado2 = new Leilao("XBox", 700.00, mauricio, false);
        encerrado1.encerra();
        encerrado2.encerra();

        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(encerrado1);
        leilaoDao.salvar(encerrado2);

        long total = leilaoDao.total();

        assertEquals(0, total);
    }

    @Test
    public void deveRetornarApenasLeiloesNaoUsados() {
        Usuario mauricio = new Usuario("Mauricio", "mauricio@hotmail.com");

        Leilao novo = new Leilao("Geladeira", 1500.00, mauricio, false);
        Leilao usado = new Leilao("XBox", 700.00, mauricio, true);

        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(novo);
        leilaoDao.salvar(usado);

        List<Leilao> novos = leilaoDao.novos();

        assertThat(novos.size(), is(1));
        assertEquals(novo, novos.get(0));
    }

    @Test
    public void deveRetornarApenasLeiloesAntigos() {
        Usuario mauricio = new Usuario("Mauricio", "mauricio@hotmail.com");

        Leilao antigo1 = new Leilao("Geladeira", 1500.00, mauricio, false);
        Leilao antigo2 = new Leilao("Geladeira", 1500.00, mauricio, false);
        Leilao novo1 = new Leilao("XBox", 700.00, mauricio, true);
        Leilao novo2 = new Leilao("XBox", 700.00, mauricio, true);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date.valueOf(LocalDate.now().minusDays(8)));

        antigo1.setDataAbertura(calendar);
        antigo2.setDataAbertura(calendar);

        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(antigo1);
        leilaoDao.salvar(antigo2);
        leilaoDao.salvar(novo1);
        leilaoDao.salvar(novo2);

        List<Leilao> antigos = leilaoDao.antigos();

        assertThat(antigos.size(), is(2));
        assertThat(antigos, contains(antigo1, antigo2));
    }

    @Test
    public void deveDeletarLeilao() {
        Usuario mauricio = new Usuario("Mauricio", "mauricio@hotmail.com");

        Leilao leilao = new Leilao("Geladeira", 1500.00, mauricio, false);

        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(leilao);
        leilaoDao.deleta(leilao);

        session.flush();
        session.clear();

        assertNull(leilaoDao.porId(leilao.getId()));
    }

    @After
    public void setdown() {
        session.getTransaction().rollback();
        session.close();
    }
}
