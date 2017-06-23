/*
 * Demoiselle Framework
 * Copyright (C) 2013 SERPRO
 * ----------------------------------------------------------------------------
 * This file is part of Demoiselle Framework.
 * 
 * Demoiselle Framework is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this program; if not,  see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 * ----------------------------------------------------------------------------
 * Este arquivo é parte do Framework Demoiselle.
 * 
 * O Framework Demoiselle é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da GNU LGPL versão 3 como publicada pela Fundação
 * do Software Livre (FSF).
 * 
 * Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA
 * GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou
 * APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU/LGPL em português
 * para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da GNU LGPL versão 3, sob o título
 * "LICENCA.txt", junto com esse programa. Se não, acesse <http://www.gnu.org/licenses/>
 * ou escreva para a Fundação do Software Livre (FSF) Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02111-1301, USA.
 */
package br.gov.frameworkdemoiselle.behave.runner.webdriver.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import br.gov.frameworkdemoiselle.behave.config.BehaveConfig;
import br.gov.frameworkdemoiselle.behave.exception.BehaveException;
import br.gov.frameworkdemoiselle.behave.message.BehaveMessage;
import br.gov.frameworkdemoiselle.behave.runner.webdriver.WebDriverRunner;

public enum WebBrowser {

	RemoteWeb {
		@Override
		public String toString() {
			return "Remote Web Driver";
		}

		@Override
		public WebDriver getWebDriver() {
			BehaveMessage message = new BehaveMessage(WebDriverRunner.MESSAGEBUNDLE);
			try {
				if (BehaveConfig.getRunner_RemoteName().equals("")) {
					throw new BehaveException(message.getString("exception-property-not-found", "behave.runner.screen.remote.name"));
				}
				if (BehaveConfig.getRunner_RemoteUrl().equals("")) {
					throw new BehaveException(message.getString("exception-property-not-found", "behave.runner.screen.remote.url"));
				}
				DesiredCapabilities capability = new DesiredCapabilities();
				capability.setBrowserName(BehaveConfig.getRunner_RemoteName());
				if (BehaveConfig.getRunner_ProfileEnabled()) {
					// Profile para Firefox REMOTE
					if (BehaveConfig.getRunner_RemoteName().equals("firefox")) {
						FirefoxProfile profile = new FirefoxProfile(new File(BehaveConfig.getRunner_ProfilePath()));
						profile.setEnableNativeEvents(true);
						capability.setCapability(FirefoxDriver.PROFILE, profile);
					}
					// TODO: Outros navegadores
				}
				return new RemoteWebDriver(new URL(BehaveConfig.getRunner_RemoteUrl()), capability);
			} catch (MalformedURLException e) {
				throw new BehaveException(message.getString("exception-error-url", BehaveConfig.getRunner_RemoteUrl()), e);
			}
		}
	},
	GhostDriver {
		@Override
		public String toString() {
			return "Ghost Driver";
		}

		@Override
		public WebDriver getWebDriver() {
			DesiredCapabilities caps = new DesiredCapabilities();

			caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] { "--ignore-ssl-errors=true" });
			caps.setJavascriptEnabled(true);
			caps.setCapability("takesScreenshot", true);
			caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, BehaveConfig.getRunner_ScreenDriverPath());

			return new PhantomJSDriver(caps);
		}
	},
	MozillaFirefox {
		@Override
		public String toString() {
			return "Mozilla Firefox";
		}

		@Override
		public WebDriver getWebDriver() {
			if (!BehaveConfig.getRunner_ProxyURL().equals("")) {
				Proxy proxy = new Proxy();
				proxy.setProxyType(Proxy.ProxyType.PAC);
				proxy.setProxyAutoconfigUrl(BehaveConfig.getRunner_ProxyURL());
				DesiredCapabilities capabilities = new DesiredCapabilities();
				capabilities.setCapability(CapabilityType.PROXY, proxy);
				return new FirefoxDriver(capabilities);
			}

			FirefoxBinary binary = BehaveConfig.getRunner_BinaryPath().equals("") ? null : new FirefoxBinary(new File(BehaveConfig.getRunner_BinaryPath()));

			FirefoxProfile profile = BehaveConfig.getRunner_ProfileEnabled() ? new FirefoxProfile(new File(BehaveConfig.getRunner_ProfilePath())) : new FirefoxProfile();
			profile.setEnableNativeEvents(true);

			FirefoxDriver driver = binary == null ? new FirefoxDriver(profile) : new FirefoxDriver(binary, profile);

			return driver;
		}
	},
	Safari {

		// Só para windows ou mac

		@Override
		public String toString() {
			return "Safari";
		}

		@Override
		public WebDriver getWebDriver() {
			boolean driverConfigurado = !BehaveConfig.getRunner_ScreenDriverPath().isEmpty();
			System.setProperty("webdriver.safari.noinstall", String.valueOf(driverConfigurado));
			System.setProperty("webdriver.safari.driver", BehaveConfig.getRunner_ScreenDriverPath());
			return new SafariDriver();
		}

	},
	InternetExplorer {

		// https://code.google.com/p/selenium/wiki/InternetExplorerDriver

		@Override
		public String toString() {
			return "Internet Explorer";
		}

		@Override
		public WebDriver getWebDriver() {
			System.setProperty("webdriver.ie.driver", BehaveConfig.getRunner_ScreenDriverPath());
			return new InternetExplorerDriver();
		}
	},
	GoogleChrome {

		/*
		 * instalar: /usr/lib/libstdc++.so.6 e
		 * /lib/tls/i686/cmov/libc.so.6(non-Javadoc)
		 * http://askubuntu.com/questions
		 * /161284/why-does-running-this-program-on
		 * -11-10-give-a-glibc-2-15-not-found-error Comando: ldd -v /bin/sh
		 * 
		 * @see java.lang.Enum#toString()
		 */

		@Override
		public String toString() {
			return "Google Chrome";
		}

		@Override
		public WebDriver getWebDriver() {
			String driver = BehaveConfig.getRunner_ScreenDriverPath();
			System.setProperty("webdriver.chrome.driver", driver);

			// Nova configuração necessária para funcionar o Profile no Chromium
			ChromeOptions chromeOptions = new ChromeOptions();
			if (BehaveConfig.getRunner_ScreenCommandLineOptions().equals("")) {
				if (BehaveConfig.getRunner_ProfileEnabled()) {
					String profile = "user-data-dir=".concat(BehaveConfig.getRunner_ProfilePath());
					chromeOptions.addArguments(profile);
				} else {
					chromeOptions.addArguments("--disable-extensions");
				}
			} else {
				// Adiciona os parâmtros enviados pelo usuário
				chromeOptions.addArguments(BehaveConfig.getRunner_ScreenCommandLineOptions());
			}

			// Nova configuração necessária para definir o caminho do browser a
			// ser executado. Podemos dessa forma executar o Chrome ou o
			// Chromium instalados na mesma máquina.
			if (!BehaveConfig.getRunner_BinaryPath().equals("")) {
				chromeOptions.setBinary(BehaveConfig.getRunner_BinaryPath());
			}

			return new ChromeDriver(chromeOptions);
		}
	},
	HtmlUnit {
		@Override
		public String toString() {
			return "Html Unit (Simulado)";
		}

		@Override
		public WebDriver getWebDriver() {
			return new HtmlUnitDriver(true);
		}
	};

	public abstract WebDriver getWebDriver();
}
