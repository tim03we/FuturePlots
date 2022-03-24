package tim03we.futureplots.provider.economy;

/*
 * This software is distributed under "GNU General Public License v3.0".
 * This license allows you to use it and/or modify it but you are not at
 * all allowed to sell this plugin at any cost. If found doing so the
 * necessary action required would be taken.
 *
 * FuturePlots is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License v3.0 for more details.
 *
 * You should have received a copy of the GNU General Public License v3.0
 * along with this program. If not, see
 * <https://opensource.org/licenses/GPL-3.0>.
 */

import net.lldv.llamaeconomy.LlamaEconomy;
import tim03we.futureplots.provider.EconomyProvider;

public class LlamaEconomyProvider implements EconomyProvider {

    @Override
    public void reduceMoney(String username, double amount) {
        LlamaEconomy.getAPI().reduceMoney(username, amount);
    }

    @Override
    public double getMoney(String username) {
        return LlamaEconomy.getAPI().getMoney(username);
    }
}
