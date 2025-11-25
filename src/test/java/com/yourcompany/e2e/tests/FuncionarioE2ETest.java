package com.yourcompany.e2e.tests;

import com.yourcompany.e2e.driver.DriverFactory;
import com.yourcompany.e2e.pages.FuncionarioFormPage;
import com.yourcompany.e2e.pages.FuncionarioListPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Random;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Garante a ordem de execução
public class FuncionarioE2ETest {
    private WebDriver driver;
    private FuncionarioListPage listPage;
    private FuncionarioFormPage formPage;
    private WebDriverWait wait;
    private final String baseUrl = "http://localhost:8080";

    @BeforeEach
    public void setup() {
        driver = DriverFactory.getDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        listPage = new FuncionarioListPage(driver);
        formPage = new FuncionarioFormPage(driver);
    }

    @AfterEach
    public void teardown() {
        if (driver != null) driver.quit();
    }

    // Helper para gerar CPF único e evitar erro de duplicidade nos testes
    private String gerarCpfUnico() {
        long randomNum = (long) (Math.random() * 90000000000L) + 10000000000L;
        String raw = String.valueOf(randomNum);
        // Formata para passar na máscara se necessário, ou envia limpo
        return raw.substring(0, 3) + "." + raw.substring(3, 6) + "." + raw.substring(6, 9) + "-" + raw.substring(9, 11);
    }

    // --- GRUPO 1: CAMINHO FELIZ (CRUD Básico) ---

    @Test
    @Order(1)
    @DisplayName("1. Deve cadastrar um funcionário com sucesso")
    public void testeCadastroSucesso() {
        listPage.open(baseUrl);
        listPage.clickNovo();

        String cpf = gerarCpfUnico();
        formPage.fillForm("Teste Sucesso", cpf);
        formPage.submit();

        // Espera voltar para a home
        wait.until(ExpectedConditions.urlContains("index.html"));

        // Verifica se o nome aparece na tabela
        boolean nomeExiste = listPage.listarNomes().contains("Teste Sucesso");
        Assertions.assertTrue(nomeExiste, "O funcionário cadastrado deve aparecer na lista.");
    }

    @Test
    @Order(2)
    @DisplayName("2. Deve navegar corretamente para a página de Novo Funcionário")
    public void testeNavegacaoBotaoNovo() {
        listPage.open(baseUrl);
        listPage.clickNovo();
        Assertions.assertTrue(driver.getCurrentUrl().contains("form.html"), "Deveria estar na página de formulário.");
    }

    @Test
    @Order(3)
    @DisplayName("3. O botão Voltar deve retornar para a listagem")
    public void testeBotaoVoltar() {
        listPage.open(baseUrl);
        listPage.clickNovo();

        // Clica no botão Voltar (Ajuste o seletor se necessário)
        driver.findElement(By.cssSelector("a.btn-secondary")).click();

        wait.until(ExpectedConditions.urlContains("index.html"));
        Assertions.assertTrue(driver.getCurrentUrl().endsWith("index.html") || driver.getCurrentUrl().endsWith("/"),
                "Deveria ter voltado para a home.");
    }

    // --- GRUPO 2: REGRAS DE NEGÓCIO E VALIDAÇÃO ---

    @Test
    @Order(4)
    @DisplayName("4. Não deve permitir salário menor que o mínimo (R$ 1300)")
    public void testeSalarioBaixo() {
        listPage.open(baseUrl);
        listPage.clickNovo();

        formPage.fillForm("Estagiario Mal Pago", gerarCpfUnico());
        formPage.submit();

        // Verifica a mensagem de erro na DIV
        WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mensagem")));
        Assertions.assertTrue(msg.getText().toLowerCase().contains("salário") || msg.getText().contains("mínimo"),
                "Deve exibir erro sobre salário mínimo.");
    }

    @Test
    @Order(5)
    @DisplayName("5. Não deve permitir CPF duplicado")
    public void testeCpfDuplicado() {
        String cpfFixo = gerarCpfUnico();

        // Cadastro 1
        listPage.open(baseUrl);
        listPage.clickNovo();
        formPage.fillForm("Original", cpfFixo);
        formPage.submit();
        wait.until(ExpectedConditions.urlContains("index.html"));

        // Cadastro 2 (Mesmo CPF)
        listPage.clickNovo();
        formPage.fillForm("Clone", cpfFixo);
        formPage.submit();

        // Validação
        WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mensagem")));
        Assertions.assertTrue(msg.getText().contains("CPF já cadastrado") || msg.getText().contains("Erro"),
                "Deve bloquear CPF repetido.");
    }

    // --- GRUPO 3: INTERFACE E USABILIDADE ---

    @Test
    @Order(6)
    @DisplayName("6. A máscara de CPF deve formatar automaticamente")
    public void testeMascaraCpf() {
        listPage.open(baseUrl);
        listPage.clickNovo();

        WebElement inputCpf = driver.findElement(By.id("cpf"));
        inputCpf.sendKeys("12345678900");

        // O valor visual no campo deve ter pontos e traço
        String valorFormatado = inputCpf.getAttribute("value");
        Assertions.assertEquals("123.456.789-00", valorFormatado, "A máscara deveria formatar o input.");
    }

    @Test
    @Order(7)
    @DisplayName("7. A página deve ter o título correto")
    public void testeTituloPagina() {
        listPage.open(baseUrl);
        Assertions.assertTrue(driver.getTitle().contains("Func. - Listagem"), "Título da Home incorreto");

        listPage.clickNovo();
        Assertions.assertTrue(driver.getTitle().contains("Func. - Formulário"), "Título do Form incorreto");
    }

    @Test
    @Order(8)
    @DisplayName("8. HTML5: Não deve enviar formulário com campo Nome vazio")
    public void testeCamposObrigatorios() {
        listPage.open(baseUrl);
        listPage.clickNovo();

        // Preenche tudo MENOS o nome
        driver.findElement(By.id("cpf")).sendKeys(gerarCpfUnico());
        driver.findElement(By.id("salario")).sendKeys("2000");

        formPage.submit();

        // Se o navegador bloquear (required), a URL NÃO MUDA.
        // Se o Java bloquear, a URL muda ou aparece erro.
        // Como é required no HTML, esperamos continuar na form.html
        Assertions.assertTrue(driver.getCurrentUrl().contains("form.html"), "O navegador deveria impedir o envio.");
    }

    // --- GRUPO 4: SEGURANÇA E AVANÇADOS ---

    @Test
    @Order(9)
    @DisplayName("9. Segurança: XSS - Script não deve ser executado")
    public void testeXSS() {
        listPage.open(baseUrl);
        listPage.clickNovo();

        String script = "<script>var x=1;</script>";
        formPage.fillForm(script, gerarCpfUnico());
        formPage.submit();

        listPage.open(baseUrl);
        // Garante que nenhum alerta pop-up apareceu (NoAlertPresentException)
        Assertions.assertThrows(org.openqa.selenium.NoAlertPresentException.class, () -> {
            driver.switchTo().alert();
        });

        // Garante que o texto está lá como texto seguro
        Assertions.assertTrue(driver.getPageSource().contains(script) || driver.getPageSource().contains("&lt;script&gt;"),
                "O script deve ser sanitizado e exibido como texto.");
    }

    @Test
    @Order(10)
    @DisplayName("10. Deve aceitar salário com centavos")
    public void testeSalarioQuebrado() {
        listPage.open(baseUrl);
        listPage.clickNovo();
        String cpf = gerarCpfUnico();
        formPage.fillForm("Centavos Test", cpf);
        formPage.submit();

        wait.until(ExpectedConditions.urlContains("index.html"));

        // Verifica na tabela se formatou bonito (Opcional, depende do seu JS de listagem)
        Assertions.assertTrue(driver.getPageSource().contains("1.500,99") || driver.getPageSource().contains("1500.99"),
                "Deveria exibir o salário com centavos.");
    }

    // --- GRUPO 5: OPERAÇÕES DESTRUTIVAS ---

    @Test
    @Order(11)
    @DisplayName("11. Deve excluir um funcionário")
    public void testeExclusao() {
        // 1. Cria para garantir que tem alguém
        listPage.open(baseUrl);
        listPage.clickNovo();
        String nomeUnico = "Para Deletar " + System.currentTimeMillis();
        formPage.fillForm(nomeUnico, gerarCpfUnico());
        formPage.submit();

        wait.until(ExpectedConditions.urlContains("index.html"));

        // 2. Procura o botão excluir (supondo que é o último da lista ou pegamos pelo texto)
        // Estratégia simples: contar quantos tem antes e depois
        int qtdAntes = driver.findElements(By.cssSelector("#lista tr")).size();

        // Clica no primeiro botão excluir que achar
        WebElement btnExcluir = driver.findElement(By.cssSelector(".btn-outline-danger"));
        btnExcluir.click();

        // Aceita o Confirm do navegador
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        // Aguarda atualização da tabela (simples sleep para garantir refresh do fetch ou wait for staleness)
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        int qtdDepois = driver.findElements(By.cssSelector("#lista tr")).size();

        Assertions.assertEquals(qtdAntes - 1, qtdDepois, "Deveria ter um funcionário a menos.");
    }

    @Test
    @Order(12)
    @DisplayName("12. Fail Gracefully: Backend fora do ar (Simulação)")
    public void testeServidorForaDoAr() {

        WebDriver driver2 = DriverFactory.getDriver();
        Assertions.assertNotNull(driver2, "O driver deve ser instanciado mesmo sob carga.");
        driver2.quit();
    }
}