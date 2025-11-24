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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FuncionarioE2ETest {
    private WebDriver driver;
    private FuncionarioListPage listPage;
    private FuncionarioFormPage formPage;
    private final String baseUrl = "http://localhost:8080";

    @BeforeEach
    public void setup() {
        driver = DriverFactory.getDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();
        listPage = new FuncionarioListPage(driver);
        formPage = new FuncionarioFormPage(driver);

        // Limpa dados (opcional, depende da persistência)
        // driver.get(baseUrl + "/index.html");
    }

    @AfterEach
    public void teardown() {
        if (driver != null) driver.quit();
    }

    // --- TESTE DE SEGURANÇA (XSS) ---
    // Requisito: "Fuzz testing para explorar vulnerabilidades"
    @Test
    @DisplayName("Segurança: Sistema deve sanitizar scripts maliciosos (XSS)")
    public void testeSegurancaXSS() {
        listPage.open(baseUrl);
        listPage.clickNovo();

        // Tenta injetar um script no nome
        String scriptMalicioso = "<script>alert('HACKED')</script>";
        formPage.fillForm(scriptMalicioso, "999.999.999-99");
        formPage.submit();

        // Vai para a lista e verifica se o script executou ou se foi tratado como texto
        listPage.open(baseUrl);

        // Se o Selenium encontrar um Alert real aqui, o teste falha (significa que fomos hackeados)
        Assertions.assertThrows(org.openqa.selenium.NoAlertPresentException.class, () -> {
            driver.switchTo().alert();
        });

        // Opcional: Verificar se o texto aparece "escapado" na tabela, não vazio
        // Isso prova que o sistema aceitou o dado mas não executou o código
        boolean textoVisivel = driver.getPageSource().contains(scriptMalicioso)
                || driver.getPageSource().contains("&lt;script&gt;");
        Assertions.assertTrue(textoVisivel, "O script deveria ser exibido como texto seguro, não executado.");
    }

    // --- TESTE DE ROBUSTEZ (FAIL GRACEFULLY) ---
    // Requisito: "Implementar fail early e fail gracefully"
    @Test
    @DisplayName("Robustez: Deve exibir mensagem amigável ao tentar duplicar CPF")
    public void testeTratamentoDeErroDuplicidade() {
        // 1. Cria o primeiro funcionário
        listPage.open(baseUrl);
        listPage.clickNovo();
        formPage.fillForm("Funcionario Original", "111.111.111-11");
        formPage.submit();

        // Espera redirecionar e voltar para form
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("index.html"));

        // 2. Tenta criar outro com O MESMO CPF
        listPage.clickNovo();
        formPage.fillForm("Clone Malicioso", "111.111.111-11");
        formPage.submit();

        // 3. Validação: NÃO deve sair da página e deve mostrar erro na DIV
        // A URL deve continuar sendo form.html
        Assertions.assertTrue(driver.getCurrentUrl().contains("form.html"));

        // Captura a mensagem de erro na interface (agora que tiramos o alert nativo)
        WebElement msgErro = driver.findElement(By.id("mensagem"));
        wait.until(ExpectedConditions.visibilityOf(msgErro));

        String textoErro = msgErro.getText();
        System.out.println("Erro capturado: " + textoErro);

        // Verifica se a mensagem é útil para o usuário (Fail Gracefully)
        Assertions.assertTrue(textoErro.contains("CPF já cadastrado") || textoErro.contains("Erro"),
                "A mensagem de erro deve ser clara para o usuário");
    }
}