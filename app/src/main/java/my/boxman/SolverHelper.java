package my.boxman;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.Menu;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SolverHelper {

    public static Map<Integer, Solver> getSolvers(Context context, boolean optimizers) {

        Intent solverIntent;
        if (!optimizers) {
            solverIntent = new Intent("nl.joriswit.sokosolver.SOLVE");
        } else {
            solverIntent = new Intent("nl.joriswit.sokosolver.OPTIMIZE");
        }

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(solverIntent, PackageManager.MATCH_DEFAULT_ONLY);

        Map<Integer, Solver> solverMap = new HashMap<>();
        for (ResolveInfo act : activities) {
            solverMap.put(View.generateViewId(), new Solver(act.activityInfo.packageName, act.loadLabel(packageManager)));
        }
        return solverMap;
    }

    public static Solver findSolverByDisplayName(Map<Integer, Solver> solverMap, String displayNameToFind) {
        for (Map.Entry<Integer, SolverHelper.Solver> solver : solverMap.entrySet()) {
            String displayName = solver.getValue().getDisplayName().toString();
            if (displayNameToFind.toLowerCase().contains(displayName.toLowerCase())) {
                return solver.getValue();
            }
        }
        return null;
    }

    public static String setImportSolver(Map<Integer, Solver> solverMap, String state, Context context, boolean includeDatetime) {
        String datetime = "";
        if (includeDatetime) {
            datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
        }
        Solver solver = findSolverByDisplayName(solverMap, state);
        if (solver != null) {
            return "[" + solver.getDisplayName() + "]" + datetime;
        }
        else if (state.toLowerCase().contains(context.getString(R.string.import2))) {
            return "[" + context.getString(R.string.import2) + "]" + datetime;
        } else {
            return datetime;
        }
    }

    public static int addSolverMenuItem(Menu menu, int order, Map<Integer, Solver> solverMap, boolean optimizer) {

        int id = Menu.NONE;
        if (solverMap.size() > 1) { // Create a sub-menu if there is more than one solver
            id = View.generateViewId();
            menu = menu.addSubMenu(Menu.NONE, id, order, optimizer ? R.string.optimization : R.string.player_solver);
            order = Menu.NONE;
        }
        // Create menu items
        for (Map.Entry<Integer, SolverHelper.Solver> solver : solverMap.entrySet()) {
            menu.add(Menu.NONE, solver.getKey(), order, solver.getValue().getDisplayName());
            if (solverMap.size() == 1) {
                id = solver.getKey();
            }
        }
        return id;
    }

    public static class Solver {
        String m_PackageName;
        CharSequence m_DisplayName;

        public Solver(String packageName, CharSequence displayName) {
            m_PackageName = packageName;
            m_DisplayName = displayName;
        }

        String getPackageName() {
            return m_PackageName;
        }

        CharSequence getDisplayName() {
            return m_DisplayName;
        }
    }
}
