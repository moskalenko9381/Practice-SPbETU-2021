package spbetu.prim.cli.command.algorithm;

import spbetu.prim.cli.command.Command;
import spbetu.prim.cli.viewmodel.ViewModel;
import spbetu.prim.exception.GraphInputException;

public class RunAlgorithmCommand extends Command {

    @Override
    public boolean execute(ViewModel viewModel) throws GraphInputException {
        if (viewModel == null)
            return false;
        viewModel.runAlgorithm();
        return true;
    }
}
