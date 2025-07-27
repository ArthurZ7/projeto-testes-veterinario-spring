package com.cod3r.gerenciadorfuncionarios;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CadastrarVeterinarioTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    @BeforeEach
    public void setUp() {
        // Configurações do Chrome para executar em modo headless (opcional)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Remove esta linha se quiser ver o browser
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testCadastrarVeterinario() {
        // Navegar para a página de cadastro
        driver.get(baseUrl + "/form");

        // Aguardar o carregamento da página
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nome")));

        // Preencher o formulário
        WebElement nomeField = driver.findElement(By.id("nome"));
        WebElement emailField = driver.findElement(By.id("inputEmail"));
        WebElement especialidadeField = driver.findElement(By.id("inputEspecialidade"));
        WebElement salarioField = driver.findElement(By.id("inputSalario"));

        nomeField.sendKeys("Dr. João Silva");
        emailField.sendKeys("joao.silva@veterinaria.com");
        especialidadeField.sendKeys("Cirurgia Veterinária");
        salarioField.sendKeys("5000.00");

        // Submeter o formulário
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // Verificar se foi redirecionado para a página home (conforme o controller)
        wait.until(ExpectedConditions.urlContains("/home"));
        String currentUrl = driver.getCurrentUrl();
        
        assertTrue(currentUrl.contains("/home"), 
            "Deveria ter sido redirecionado para a página home após o cadastro");
    }

    @Test
    public void testCadastrarVeterinarioComCamposObrigatorios() {
        // Navegar para a página de cadastro
        driver.get(baseUrl + "/form");

        // Aguardar o carregamento da página
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nome")));

        // Tentar submeter formulário sem preencher campos obrigatórios
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // Verificar se ainda está na mesma página (validação HTML5)
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/form"), 
            "Deveria permanecer na página de formulário quando campos obrigatórios não são preenchidos");
    }

    @Test
    public void testCadastrarVeterinarioComEmailInvalido() {
        // Navegar para a página de cadastro
        driver.get(baseUrl + "/form");

        // Aguardar o carregamento da página
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nome")));

        // Preencher o formulário com email inválido
        WebElement nomeField = driver.findElement(By.id("nome"));
        WebElement emailField = driver.findElement(By.id("inputEmail"));
        WebElement especialidadeField = driver.findElement(By.id("inputEspecialidade"));
        WebElement salarioField = driver.findElement(By.id("inputSalario"));

        nomeField.sendKeys("Dr. Maria Santos");
        emailField.sendKeys("email-invalido"); // Email sem formato válido
        especialidadeField.sendKeys("Dermatologia Veterinária");
        salarioField.sendKeys("4500.00");

        // Tentar submeter o formulário
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // Verificar se ainda está na mesma página devido à validação de email
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/form"), 
            "Deveria permanecer na página de formulário quando email é inválido");
    }

    @Test
    public void testPreenchimentoFormularioCadastro() {
        // Navegar para a página de cadastro
        driver.get(baseUrl + "/form");

        // Aguardar o carregamento da página
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nome")));

        // Verificar se todos os campos estão presentes
        assertTrue(driver.findElement(By.id("nome")).isDisplayed(), 
            "Campo nome deveria estar visível");
        assertTrue(driver.findElement(By.id("inputEmail")).isDisplayed(), 
            "Campo email deveria estar visível");
        assertTrue(driver.findElement(By.id("inputEspecialidade")).isDisplayed(), 
            "Campo especialidade deveria estar visível");
        assertTrue(driver.findElement(By.id("inputSalario")).isDisplayed(), 
            "Campo salário deveria estar visível");

        // Verificar se o botão de cadastrar está presente
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        assertTrue(submitButton.isDisplayed(), "Botão de cadastrar deveria estar visível");
        assertEquals("Cadastrar", submitButton.getText(), 
            "Texto do botão deveria ser 'Cadastrar'");
    }
}
