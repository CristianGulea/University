using System;
using System.Data;
using System.Data.SqlClient;
using System.Windows.Forms;

namespace SGBDL1
{
    public partial class Form1 : Form
    {
        SqlConnection conn = new SqlConnection("Data Source=LAPTOP-RL04RJFJ\\SQLEXPRESS;Initial Catalog=SGBDMagazinElectronice;Integrated Security=True");
        DataSet ds = new DataSet();
        SqlDataAdapter adapter = new SqlDataAdapter();
        private int ManagerID;

        public Form1()
        {
            InitializeComponent();
        }

        private void AfisareMagazine_Click(object sender, EventArgs e)
        {
            adapter.SelectCommand = new SqlCommand("select * from Magazin", conn);
            ds.Clear();
            adapter.Fill(ds);
            dataGridView1.DataSource = ds.Tables[0];
        }

        private void AfisareManageri_Click(object sender, EventArgs e)
        {
            adapter.SelectCommand = new SqlCommand("select * from Manager", conn);
            DataTable dataTable = new DataTable();
            adapter.Fill(dataTable);
            dataGridView2.DataSource = dataTable;
        }

        private void ButonAdaugare_Click(object sender, EventArgs e)
        {
            try
            {
                adapter.InsertCommand = new SqlCommand("insert into Manager(Nume, Prenume, Salar, MId) values (@nume, @prenume, @salar, @mid)", conn);
                adapter.InsertCommand.Parameters.Add("@nume", SqlDbType.VarChar).Value = textBoxNume.Text;
                adapter.InsertCommand.Parameters.Add("@prenume", SqlDbType.VarChar).Value = textBoxPrenume.Text;
                adapter.InsertCommand.Parameters.Add("@salar", SqlDbType.Int).Value = Int32.Parse(textBoxSalar.Text);
                adapter.InsertCommand.Parameters.Add("@mid", SqlDbType.Int).Value = Int32.Parse(textBoxMID.Text);
                conn.Open();
                adapter.InsertCommand.ExecuteNonQuery();
                MessageBox.Show("Adaugare s-a realizat cu succes!");
                conn.Close();
            }
            catch (Exception ex)
            {
                conn.Close();
                MessageBox.Show(ex.Message);
            }
            textBoxMID.Clear();
            textBoxSalar.Clear();
            textBoxPrenume.Clear();
            textBoxNume.Clear();
        }

        private void SetSelectedManagerID(int managerID)
        {
            this.ManagerID = managerID;
        }

        private void dataGridView2_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            textBoxMID.Clear();
            textBoxSalar.Clear();
            textBoxPrenume.Clear();
            textBoxNume.Clear();

            int id = (int)(dataGridView2.Rows[e.RowIndex].Cells[0].Value);
            var nume = dataGridView2.Rows[e.RowIndex].Cells[1].Value; 
            var prenume = dataGridView2.Rows[e.RowIndex].Cells[2].Value;
            var salar = dataGridView2.Rows[e.RowIndex].Cells[3].Value;
            var mid = dataGridView2.Rows[e.RowIndex].Cells[4].Value;

            SetSelectedManagerID(id);

            textBoxNume.AppendText(nume.ToString());
            textBoxPrenume.AppendText(prenume.ToString());
            textBoxSalar.AppendText(salar.ToString());
            textBoxMID.AppendText(mid.ToString());
          

        }

        private void button1_Click(object sender, EventArgs e)
        {
            try
            {
                adapter.UpdateCommand = new SqlCommand("update Manager set Nume = @nume, Prenume = @prenume, Salar = @salar, MId = @mid where managerID = @managerID", conn);
                adapter.UpdateCommand.Parameters.Add("@nume", SqlDbType.VarChar).Value = textBoxNume.Text;
                adapter.UpdateCommand.Parameters.Add("@prenume", SqlDbType.VarChar).Value = textBoxPrenume.Text;
                adapter.UpdateCommand.Parameters.Add("@salar", SqlDbType.Int).Value = Int32.Parse(textBoxSalar.Text);
                adapter.UpdateCommand.Parameters.Add("@mid", SqlDbType.Int).Value = Int32.Parse(textBoxMID.Text);
                adapter.UpdateCommand.Parameters.Add("@managerID", SqlDbType.Int).Value = ManagerID;
                conn.Open();
                adapter.UpdateCommand.ExecuteNonQuery();
                MessageBox.Show("Modificarea s-a realizat cu succes!");
                conn.Close();
            }
            catch (Exception ex)
            {
                conn.Close();
                MessageBox.Show(ex.Message);
            }
            textBoxMID.Clear();
            textBoxSalar.Clear();
            textBoxPrenume.Clear();
            textBoxNume.Clear();

        }

        private void button2_Click(object sender, EventArgs e)
        {
            try
            {
                adapter.DeleteCommand = new SqlCommand("delete from Manager where ManagerID = @managerID", conn);
                adapter.DeleteCommand.Parameters.Add("@managerID", SqlDbType.Int).Value = ManagerID;
                conn.Open();
                adapter.DeleteCommand.ExecuteNonQuery ();
                MessageBox.Show("Stergerea s-a realizat cu succes!");
                conn.Close ();
            }
            catch(Exception ex)
            {
                conn.Close();
                MessageBox.Show(ex.Message);
            }

            textBoxMID.Clear();
            textBoxSalar.Clear();
            textBoxPrenume.Clear();
            textBoxNume.Clear();
        }

        private void dataGridView1_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            int id = (int)dataGridView1.Rows[e.RowIndex].Cells[0].Value;
            Console.WriteLine(id);
            SqlCommand sqlCommand = new SqlCommand("select * from Manager where MId = @Mid", conn);
            SqlParameter param = new SqlParameter();
            param.ParameterName = "@Mid";
            param.Value = id;
            sqlCommand.Parameters.Add(param);
            adapter.SelectCommand = sqlCommand;
            DataTable dataTable = new DataTable();
            adapter.Fill(dataTable);
            dataGridView2.DataSource = dataTable;

            textBoxMID.Clear();
            textBoxSalar.Clear();
            textBoxPrenume.Clear();
            textBoxNume.Clear();
            textBoxMID.AppendText(dataGridView1.Rows[e.RowIndex].Cells[0].Value.ToString());
        }
    }
}
