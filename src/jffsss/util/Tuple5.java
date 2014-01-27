package jffsss.util;

public class Tuple5<A, B, C, D, E>
{
	private A _A;
	private B _B;
	private C _C;
	private D _D;
	private E _E;

	public Tuple5(A _A, B _B, C _C, D _D, E _E)
	{
		this._A = _A;
		this._B = _B;
		this._C = _C;
		this._D = _D;
		this._E = _E;
	}

	public A getA()
	{
		return this._A;
	}

	public B getB()
	{
		return this._B;
	}

	public C getC()
	{
		return this._C;
	}

	public D getD()
	{
		return this._D;
	}

	public E getE()
	{
		return this._E;
	}
}